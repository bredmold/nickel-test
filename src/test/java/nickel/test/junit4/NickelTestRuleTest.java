package nickel.test.junit4;

import org.junit.Rule;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class NickelTestRuleTest {
    @Rule
    public NickelTestRule nickelTestRule = new NickelTestRule();

    @Test
    public void testResource() throws ClassNotFoundException, IOException {
        assertThat(nickelTestRule.testResource()
            .forTestMethod()
            .resourceExtension(".txt")
            .asString("UTF-8"))
            .isEqualTo("Itza test resource!\n");
    }

    @Test
    public void jacksonTestResource() throws ClassNotFoundException, IOException {
        assertThat(nickelTestRule.jacksonTestResource()
            .forTestMethod()
            .asJson(TestType.class))
            .isEqualTo(new TestType("value"));
    }

    @Test
    public void jaxbTestResource() throws ClassNotFoundException, JAXBException, IOException {
        assertThat(nickelTestRule.jaxbTestResource()
            .forTestMethod()
            .asXml(TestType.class))
            .isEqualTo(new TestType("xml"));
    }

    @Test
    public void yamlTestResource() throws ClassNotFoundException, IOException {
        assertThat(nickelTestRule.yamlTestResource()
            .forTestMethod()
            .asYaml(TestType.class))
            .isEqualTo(new TestType("yaml"));
    }

    public static class TestType {
        private String v;

        public TestType() {
        }

        TestType(String v) {
            this.v = v;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestType testType = (TestType) o;
            return Objects.equals(v, testType.v);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v);
        }

        @Override
        public String toString() {
            return "TestType{" +
                "v='" + v + '\'' +
                '}';
        }
    }
}
