package nickel.test.properties;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static nickel.test.properties.PropertiesTestResource.propertiesTestResource;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesTestResourceTest {
    @Test
    public void properties() throws ClassNotFoundException, IOException {
        Properties properties = propertiesTestResource()
            .forTestMethod()
            .asProperties();

        assertThat(properties).isNotNull();
        assertThat(properties).hasEntrySatisfying("value1", value -> assertThat(value).isEqualTo("42"));
        assertThat(properties).hasEntrySatisfying("value2", value -> assertThat(value).isEqualTo("508"));
        assertThat(properties.keySet()).hasSize(2);
    }

    @Test
    public void xmlProperties() throws ClassNotFoundException, IOException {
        Properties properties = propertiesTestResource()
            .forTestMethod()
            .asXmlProperties();

        assertThat(properties).isNotNull();
        assertThat(properties).hasEntrySatisfying("value1", value -> assertThat(value).isEqualTo("42"));
        assertThat(properties).hasEntrySatisfying("value2", value -> assertThat(value).isEqualTo("508"));
        assertThat(properties.keySet()).hasSize(2);
    }
}
