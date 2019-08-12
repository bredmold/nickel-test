package nickel.test.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nickel.test.annotations.BindingContext;
import nickel.test.annotations.NickelTestResource;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NickelTestInjectorTest {
    private NickelTestInjector nickelTestInjector;

    @NickelTestResource(with = NickelTestResource.Binding.jackson)
    private TestType jsonInstance;

    @NickelTestResource(with = NickelTestResource.Binding.jackson)
    private List<TestType> jsonListInstance;

    @BindingContext
    private ObjectMapper objectMapper;

    @NickelTestResource(with = NickelTestResource.Binding.jaxb)
    private TestType jaxbInstance;

    @NickelTestResource(with = NickelTestResource.Binding.yaml)
    private TestType yamlInstance;

    @BindingContext
    private Yaml yaml;

    @NickelTestResource(
        resourceName = "yamlInstance",
        resourceExtension = ".yaml")
    private String stringInstance;

    @NickelTestResource(
        resourceName = "yamlInstance",
        resourceExtension = ".yaml")
    private byte[] byteInstance;

    @NickelTestResource(
        resourcePath = "/NickelTestInjectorTest",
        resourceName = "yamlInstance",
        resourceExtension = ".yaml")
    private Properties propertiesInstance;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

        yaml = new Yaml(new TestTypeConstructor());

        nickelTestInjector = new NickelTestInjector(this);
    }

    @Test
    public void injectSimpleJsonField() {
        assertThat(jsonInstance).isNull();

        nickelTestInjector.injectAllFields();

        Instant expectedInstant = Instant.parse("2019-07-27T21:20:00.000Z");
        TestType expectedInstance = new TestType(expectedInstant);

        assertThat(jsonInstance).isEqualTo(expectedInstance);
    }

    @Test
    public void injectJsonListField() {
        assertThat(jsonListInstance).isNull();

        nickelTestInjector.injectAllFields();

        Instant expectedInstant = Instant.parse("2019-07-27T21:20:00.000Z");
        TestType expectedInstance = new TestType(expectedInstant);

        assertThat(jsonListInstance).isEqualTo(singletonList(expectedInstance));
    }

    @Test
    public void injectSimpleJaxbField() {
        assertThat(jaxbInstance).isNull();

        nickelTestInjector.injectAllFields();

        Instant expectedInstant = Instant.parse("2019-07-27T21:20:00.000Z");
        TestType expectedInstance = new TestType(expectedInstant);

        assertThat(jaxbInstance).isEqualTo(expectedInstance);
    }

    @Test
    public void injectSimpleYamlField() {
        assertThat(yamlInstance).isNull();

        nickelTestInjector.injectAllFields();

        Instant expectedInstant = Instant.parse("2019-07-27T21:20:00.000Z");
        TestType expectedInstance = new TestType(expectedInstant);

        assertThat(yamlInstance).isEqualTo(expectedInstance);
    }

    @Test
    public void injectStringField() {
        assertThat(stringInstance).isNull();

        nickelTestInjector.injectAllFields();

        String expected = "when: 2019-07-27T21:20:00.000Z\n";

        assertThat(stringInstance).isEqualTo(expected);
    }

    @Test
    public void injectByteArrayField() {
        assertThat(byteInstance).isNull();

        nickelTestInjector.injectAllFields();

        byte[] expected = "when: 2019-07-27T21:20:00.000Z\n".getBytes(StandardCharsets.UTF_8);

        assertThat(byteInstance).isEqualTo(expected);
    }

    @Test
    public void injectPropertiesField() {
        assertThat(propertiesInstance).isNull();

        nickelTestInjector.injectAllFields();

        Properties expected = new Properties();
        expected.put("when", "2019-07-27T21:20:00.000Z");

        assertThat(propertiesInstance).isEqualTo(expected);
    }

    @Test
    public void testName() {
        assertThat(nickelTestInjector.testName())
            .isNotPresent();
    }

    @Test
    public void suiteName() {
        assertThat(nickelTestInjector.suiteName(false))
            .isEqualTo(NickelTestInjectorTest.class.getSimpleName());

        assertThat(nickelTestInjector.suiteName(true))
            .isEqualTo(NickelTestInjectorTest.class.getName());
    }

    @Test
    public void notBound() {
        FailClass failClass = new FailClass();

        assertThatThrownBy(() -> NickelTestInjector.injectAllFields(failClass))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Don't know how to interpret type ")
            .hasMessageEndingWith(" without binding");
    }

    public static class TestType {
        private Instant when;

        public TestType() {
        }

        TestType(Instant when) {
            this.when = when;
        }

        public Instant getWhen() {
            return when;
        }

        public void setWhen(Instant when) {
            this.when = when;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestType testType = (TestType) o;
            return Objects.equals(when, testType.when);
        }

        @Override
        public int hashCode() {
            return Objects.hash(when);
        }

        @Override
        public String toString() {
            return "TestType{" +
                "when=" + when +
                '}';
        }
    }

    static class TestTypeConstructor extends Constructor {
        @Override
        protected Object constructObject(Node node) {
            if (Instant.class.isAssignableFrom(node.getType())) {
                return Instant.parse(((ScalarNode) node).getValue());
            } else {
                return super.constructObject(node);
            }
        }
    }

    static class FailClass {
        @NickelTestResource
        private TestType notBound;
    }
}
