package nickel.test.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import nickel.test.NickelTestException;
import nickel.test.annotations.BindingContext;
import nickel.test.annotations.NickelTestResource;
import nickel.test.jackson.JacksonTestResource;
import nickel.test.jaxb.JaxbTestResource;
import nickel.test.strategy.ResourceNamingStrategy;
import nickel.test.yaml.YamlTestResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class NickelTestInjector implements ResourceNamingStrategy {
    /**
     * Inject all the annotated fields of the given test instance.
     *
     * @param testInstance The test instance to inject
     */
    public static void injectAllFields(Object testInstance) {
        new NickelTestInjector(testInstance).injectAllFields();
    }

    private final Object testInstance;
    private final Class<?> testClass;

    /**
     * Construct an injector for a given test instance
     *
     * @param testInstance The test instance to inject - may not be null
     */
    public NickelTestInjector(Object testInstance) {
        this.testInstance = requireNonNull(testInstance, "testInstance");

        testClass = testInstance.getClass();
    }

    @Override
    public Optional<String> testName() {
        return Optional.empty();
    }

    @Override
    public String suiteName(boolean fullyQualified) {
        return fullyQualified
            ? testClass.getName()
            : testClass.getSimpleName();
    }

    /**
     * Inject all the fields for the test instance
     */
    public void injectAllFields() {
        for (Field field : FieldUtils.getFieldsListWithAnnotation(testClass, NickelTestResource.class)) {
            try {
                injectField(field);
            } catch (IllegalAccessException e) {
                throw new NickelTestException(e);
            }
        }
    }

    /**
     * Load a single resource, given the resource meta-data
     *
     * @param nickelTestResource Resource annotation
     * @param resourceType       Java type of the resource to load
     * @param name               Name of the Java entity to which the annotation is attached (e.g. method, field)
     * @return Fully initialized resource object
     */
    public Object loadResource(NickelTestResource nickelTestResource, Type resourceType, String name) {
        try {
            switch (nickelTestResource.with()) {
                case none:
                    return loadStandardResource(nickelTestResource, resourceType, name);
                case jackson:
                    return loadJacksonResource(nickelTestResource, resourceType, name);
                case jaxb:
                    return loadJaxbResource(nickelTestResource, resourceType, name);
                case yaml:
                    return loadYamlResource(nickelTestResource, resourceType, name);
                default:
                    throw new NickelTestException("Unknown binding: " + nickelTestResource.with());
            }
        } catch (IllegalAccessException | IOException | JAXBException | ClassNotFoundException e) {
            throw new NickelTestException(e);
        }
    }

    private void injectField(Field field) throws IllegalAccessException {
        NickelTestResource nickelTestResource = field.getAnnotation(NickelTestResource.class);
        Object resourceValue = loadResource(nickelTestResource, field.getGenericType(), field.getName());
        field.setAccessible(true);
        field.set(testInstance, resourceValue);
    }

    private Object loadYamlResource(NickelTestResource nickelTestResource, Type resourceType, String name)
        throws IllegalAccessException, ClassNotFoundException, IOException {
        YamlTestResource yamlTestResource = new YamlTestResource(this)
            .withYaml(findBindingContext(Yaml.class));
        identifyResource(yamlTestResource, nickelTestResource, name);

        return yamlTestResource.asYaml(((Class<?>) resourceType));
    }

    private Object loadJaxbResource(NickelTestResource nickelTestResource, Type resourceType, String name)
        throws IllegalAccessException, ClassNotFoundException, JAXBException, IOException {
        JaxbTestResource jaxbTestResource = new JaxbTestResource(this)
            .withJaxbContext(findBindingContext(JAXBContext.class));
        identifyResource(jaxbTestResource, nickelTestResource, name);

        return jaxbTestResource.asXml(((Class<?>) resourceType));
    }

    private Object loadJacksonResource(NickelTestResource nickelTestResource, Type resourceType, String name)
        throws IllegalAccessException, ClassNotFoundException, IOException {
        JacksonTestResource jacksonTestResource = new JacksonTestResource(this)
            .withMapper(findBindingContext(ObjectMapper.class));
        identifyResource(jacksonTestResource, nickelTestResource, name);

        return jacksonTestResource.asJson(resourceType);
    }

    private Object loadStandardResource(NickelTestResource nickelTestResource, Type resourceType, String name)
        throws IOException {
        nickel.test.NickelTestResource<nickel.test.NickelTestResource> standardResource =
            new nickel.test.NickelTestResource<>(this);
        identifyResource(standardResource, nickelTestResource, name);

        if (TypeUtils.isAssignable(resourceType, byte[].class)) {
            return standardResource.asBytes();
        } else if (TypeUtils.isAssignable(resourceType, String.class)) {
            return standardResource.asString(nickelTestResource.encoding());
        } else if (TypeUtils.isAssignable(resourceType, Properties.class)) {
            return standardResource.asProperties();
        } else if (TypeUtils.isAssignable(resourceType, InputStream.class)) {
            return standardResource.asStream();
        } else {
            throw new IllegalArgumentException(String.format(
                "Don't know how to interpret type %s without binding", resourceType));
        }
    }

    /**
     * Initialize the resource naming details, including any overrides that may come in from the annotation.
     *
     * @param initializedResource Initialized {@link nickel.test.NickelTestResource} instance
     * @param nickelTestResource  Resource annotation with additional naming information
     * @param name                The name of Java entity to which the resource annotation is attached
     */
    private void identifyResource(
        nickel.test.NickelTestResource<?> initializedResource,
        NickelTestResource nickelTestResource,
        String name) {
        if (StringUtils.isNotBlank(nickelTestResource.resourcePath())) {
            initializedResource.resourcePath(nickelTestResource.resourcePath());
        } else {
            initializedResource.forTestClass(nickelTestResource.includeFullPath());
        }

        if (StringUtils.isNotBlank(nickelTestResource.resourceName())) {
            initializedResource.resourceName(nickelTestResource.resourceName());
        } else {
            initializedResource.resourceName(name);
        }

        if (StringUtils.isNotBlank(nickelTestResource.resourceExtension())) {
            initializedResource.resourceExtension(nickelTestResource.resourceExtension());
        }
    }

    /**
     * If there's a binding context in the test case, locate it and return it.
     *
     * @param bindingContextClass The desired type of the binding context
     * @param <T>                 Type parameter for the binding context
     * @return The binding context, or null if none could be found
     */
    private <T> T findBindingContext(Class<T> bindingContextClass) throws IllegalAccessException {
        if (testInstance != null) {
            List<Field> bindingContextFields = FieldUtils.getFieldsListWithAnnotation(
                testInstance.getClass(), BindingContext.class);

            if (CollectionUtils.isNotEmpty(bindingContextFields)) {
                for (Field bindingContextField : bindingContextFields) {
                    if (bindingContextClass.isAssignableFrom(bindingContextField.getType())) {
                        bindingContextField.setAccessible(true);

                        //noinspection unchecked
                        T context = (T) bindingContextField.get(testInstance);
                        if (context != null) {
                            return context;
                        }
                    }
                }
            }
        }

        return null;
    }
}
