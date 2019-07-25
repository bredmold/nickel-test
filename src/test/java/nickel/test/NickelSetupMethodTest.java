package nickel.test;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static nickel.test.NickelTestResource.testResource;
import static org.assertj.core.api.Assertions.assertThat;

public class NickelSetupMethodTest {
    private String value;
    private NickelTestException nickelTestException;

    @Before
    public void setUp() throws ClassNotFoundException, IOException {
        value = testResource()
            .forTestClass()
            .resourceName("testresource")
            .asString("UTF-8");

        try {
            testResource()
                .forTestMethod()
                .asBytes();
        } catch (NickelTestException e) {
            nickelTestException = e;
        }
    }

    @Test
    public void resourceValue() {
        assertThat(value).isEqualTo("Test Resource\n");
    }

    @Test
    public void testMethodFailsDuringSetupMethod() {
        assertThat(nickelTestException).isNotNull();
        assertThat(nickelTestException).hasFieldOrPropertyWithValue("message", "Outside the scope of a test");
    }
}
