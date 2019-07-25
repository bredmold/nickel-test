package nickel.test;

import nickel.test.junit4.Junit4StackBasedNamingStrategy;
import nickel.test.strategy.ResourceNamingStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Test case resource. Local a resource for your test!
 *
 * <p>Uses a fluent API to specify a test resource, read it, and return it.</p>
 */
public class NickelTestResource<T extends NickelTestResource> {
    public static NickelTestResource<NickelTestResource> testResource() throws ClassNotFoundException {
        return new NickelTestResource<>(new Junit4StackBasedNamingStrategy());
    }

    private final ResourceNamingStrategy namingStrategy;
    private final T instance;
    private String resourceExtension = "";
    private String resourceName = "";
    private String resourcePath = "";

    private boolean includeTestClassPackageInPath = false;

    public NickelTestResource(ResourceNamingStrategy namingStrategy) throws ClassNotFoundException {
        this.namingStrategy = requireNonNull(namingStrategy, "namingStrategy");

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


    private void populateResourcePath() {
        String suiteName = namingStrategy.suiteName(includeTestClassPackageInPath);

        resourcePath = "/".concat(suiteName.replace(".", "/"));
    }

    private void populateResourceName() {
        resourceName = namingStrategy.testName()
            .orElseThrow(() -> new NickelTestException("Outside the scope of a test"));
    }

    private String fullResourcePath() {
        String partialResourceName = resourceName.concat(resourceExtension);
        return StringUtils.isBlank(resourcePath)
            ? partialResourceName
            : String.format("%s/%s", resourcePath, partialResourceName);
    }

    protected InputStream resolveStream() {
        try {
            String path = fullResourcePath();
            Class<?> testClass = Class.forName(namingStrategy.suiteName(true));
            InputStream stream = testClass.getResourceAsStream(path);
            return requireNonNull(stream, path);
        } catch (ClassNotFoundException e) {
            throw new NickelTestException(e);
        }
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
