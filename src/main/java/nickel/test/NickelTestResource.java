package nickel.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Test case resource. Local a resource for your test!
 *
 * <p>Uses a fluent API to specify a test resource, read it, and return it.</p>
 */
public class NickelTestResource<T extends NickelTestResource> {
    public static NickelTestResource<NickelTestResource> testResource() throws ClassNotFoundException {
        return new NickelTestResource<>();
    }

    private final Method testMethod;
    private final Method targetMethod;
    private final T instance;
    private String resourceExtension = "";
    private String resourceName = "";
    private String resourcePath = "";

    private boolean includeTestClassPackageInPath = false;

    protected NickelTestResource() throws ClassNotFoundException {
        testMethod = locateAnnotatedMethod(Test.class);
        targetMethod = (testMethod != null)
            ? testMethod
            : locateAnnotatedMethod(Before.class);

        if (targetMethod == null) {
            throw new NickelTestException("Unable to locate a method annotated with either @Test or @Before");
        }

        instance = (T) this;
    }

    /**
     * Locate the resource relative to the current test case
     *
     * @param includeTestClassPackageInPath If true, include the test class full package name in the resource path,
     *                                      otherwise, omit the package name from the path.
     */
    public T forTestClass(boolean includeTestClassPackageInPath) {
        this.includeTestClassPackageInPath = includeTestClassPackageInPath;
        populateResourcePath();
        return instance;
    }

    /**
     * Equivalent to calling <code>forTestClass(false)</code>
     */
    public T forTestClass() {
        return forTestClass(false);
    }

    /**
     * Locate the resource based on both test class and test method.
     *
     * @param includeTestClassPackageInPath If true, include the test class full package name in the resource path,
     *                                      otherwise, omit the package name from the path.
     */
    public T forTestMethod(boolean includeTestClassPackageInPath) {
        if (testMethod == null) {
            throw new NickelTestException("No test method");
        }

        this.includeTestClassPackageInPath = includeTestClassPackageInPath;
        populateResourcePath();
        populateResourceName();
        return instance;
    }

    /**
     * Equivalent to calling <code>forTestMethod(false)</code>
     */
    public T forTestMethod() {
        return forTestMethod(false);
    }

    /**
     * Sets the resource name to use for class-relative resources.
     *
     * @param resourceName Use this resource name when locating a resource
     */
    public T resourceName(String resourceName) {
        this.resourceName = resourceName;
        return instance;
    }

    /**
     * Sets the resource path (similar to package name). Useful if your test data isn't relative to your test class.
     *
     * <p>Use a leading / (forward slash) to specify an absolute resource path, otherwise it will be relative to
     * the test class.</p>
     *
     * @param resourcePath Full resource path. This will be evaluated using {@link Class#getResourceAsStream(String)}
     *                     for the test class.
     */
    public T resourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        return instance;
    }

    /**
     * Populate the filename extension for the test resource.
     *
     * @param resourceExtension This will be appended to the full resource name
     */
    public T resourceExtension(String resourceExtension) {
        this.resourceExtension = resourceExtension;
        return instance;
    }

    /**
     * Get the resource as an {@link InputStream}
     *
     * @return Results of {@link Class#getResourceAsStream(String)} for the fully-resolved path
     */
    public InputStream asStream() {
        return resolveStream();
    }

    /**
     * Get the test resource as a byte array
     *
     * @return Byte array containing the content of the stream
     */
    public byte[] asBytes() throws IOException {
        try (InputStream stream = resolveStream()) {
            return IOUtils.toByteArray(stream);
        }
    }

    /**
     * Get the test resource as a String
     *
     * @param encoding String encoding
     * @return String containing the content of the resource
     */
    public String asString(String encoding) throws IOException {
        try (InputStream stream = resolveStream()) {
            return IOUtils.toString(stream, encoding);
        }
    }

    /**
     * Read properties from a Java Properties file.
     *
     * @return Properties object derived from the file
     */
    public Properties asProperties() throws IOException {
        defaultResourceExtension(".properties");

        try (InputStream stream = resolveStream()) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }

    /**
     * Read properties from XML Properties file
     *
     * @return Properties object derived from the file
     */
    public Properties asXmlProperties() throws IOException {
        defaultResourceExtension(".xml");

        try (InputStream stream = resolveStream()) {
            Properties properties = new Properties();
            properties.loadFromXML(stream);
            return properties;
        }
    }

    /**
     * Examine the current thread's stack to find a target method. A target method is defined as follows:
     * <ol>
     * <li>Empty argument list</li>
     * <li>Annotated with the selected annotation</li>
     * </ol>
     *
     * @param annotationClass The annotation class we're searching for
     * @return {@link Method} reference, if the method is found, otherwise null
     */
    private static <A extends Annotation> Method locateAnnotatedMethod(Class<A> annotationClass) throws ClassNotFoundException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            Class<?> stackClass;
            Method candidateMethod;
            try {
                stackClass = Class.forName(className);
                candidateMethod = ClassUtils.getPublicMethod(stackClass, methodName);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                continue;
            }

            A targetAnnotation = candidateMethod.getAnnotation(annotationClass);
            if (targetAnnotation != null) {
                return candidateMethod;
            }
        }

        return null;
    }

    private void populateResourcePath() {
        String className = includeTestClassPackageInPath
            ? targetMethod.getDeclaringClass().getName()
            : targetMethod.getDeclaringClass().getSimpleName();

        resourcePath = "/".concat(className.replace(".", "/"));
    }

    private void populateResourceName() {
        resourceName = targetMethod.getName();
    }

    private String fullResourcePath() {
        String partialResourceName = resourceName.concat(resourceExtension);
        return StringUtils.isBlank(resourcePath)
            ? partialResourceName
            : String.format("%s/%s", resourcePath, partialResourceName);
    }

    protected InputStream resolveStream() {
        String path = fullResourcePath();
        InputStream stream = targetMethod.getDeclaringClass().getResourceAsStream(path);
        return requireNonNull(stream, path);
    }

    /**
     * For sub-classes, set the resource extension if it's not already set.
     *
     * @param resourceExtension The resource extension to use, if it's not already given
     */
    protected void defaultResourceExtension(String resourceExtension) {
        if (StringUtils.isBlank(this.resourceExtension)) {
            this.resourceExtension = resourceExtension;
        }
    }
}
