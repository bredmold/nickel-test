package nickel.test.properties;

import nickel.test.NickelTestResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Java Properties test resource.
 */
public class PropertiesTestResource extends NickelTestResource<PropertiesTestResource> {
    public static PropertiesTestResource propertiesTestResource() throws ClassNotFoundException {
        return new PropertiesTestResource();
    }

    protected PropertiesTestResource() throws ClassNotFoundException {
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
}
