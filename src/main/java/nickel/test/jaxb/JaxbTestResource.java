package nickel.test.jaxb;

import nickel.test.NickelTestResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

/**
 * JAXB Test resource for resources in XML
 */
public class JaxbTestResource extends NickelTestResource<JaxbTestResource> {
    public static JaxbTestResource jaxbTestResource() throws ClassNotFoundException {
        return new JaxbTestResource();
    }

    private JAXBContext context;

    private JaxbTestResource() throws ClassNotFoundException {
    }

    /**
     * Provide a {@link JAXBContext} instance to use for parsing.
     *
     * @param context {@link JAXBContext} instance to use for parsing
     */
    public JaxbTestResource withJaxbContext(JAXBContext context) {
        this.context = context;
        return this;
    }

    /**
     * Initialize a {@link JAXBContext} and use it to parse the test resource.
     *
     * @param targetClass The kind of object to return (binding type)
     * @param <T>         Target type
     * @return Object instance of the target type
     */
    public <T> T asXml(Class<T> targetClass) throws JAXBException {
        defaultResourceExtension(".xml");
        StreamSource streamSource = new StreamSource(resolveStream());
        return context(targetClass)
            .createUnmarshaller()
            .unmarshal(streamSource, targetClass)
            .getValue();
    }

    private JAXBContext context(Class<?> boundType) throws JAXBException {
        return (context == null)
            ? JAXBContext.newInstance(boundType)
            : context;
    }
}
