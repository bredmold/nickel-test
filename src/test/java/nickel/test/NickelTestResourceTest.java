package nickel.test;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static nickel.test.NickelTestResource.testResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NickelTestResourceTest {
    @Test
    public void testCaseBytes() throws ClassNotFoundException, IOException {
        byte[] expectedBytes = "testCaseBytes".getBytes(StandardCharsets.UTF_8);

        byte[] testBytes = testResource()
                .forTestMethod()
                .asBytes();

        assertThat(testBytes).containsExactly(expectedBytes);
    }

    @Test
    public void testClassBytes() throws ClassNotFoundException, IOException {
        byte[] expectedBytes = "stringFile".getBytes(StandardCharsets.UTF_8);

        byte[] testByes = testResource()
                .forTestClass()
                .resourceName("stringFile")
                .asBytes();

        assertThat(testByes).containsExactly(expectedBytes);
    }

    @Test
    public void noSuchResource() {
        assertThatThrownBy(() -> testResource()
                .forTestClass()
                .resourceName("nope")
                .asBytes())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("/NickelTestResourceTest/nope");
    }

    @Test
    public void testMethodString() throws ClassNotFoundException, IOException {
        String expectedString = "{\"foo\":\"bar\"}";

        String testString = testResource()
                .forTestMethod(true)
                .resourceExtension(".json")
                .asString("UTF-8");

        assertThat(testString).isEqualTo(expectedString);
    }

    @Test
    public void selectedResourceAsStream() throws IOException, ClassNotFoundException {
        byte[] expectedBytes = "file".getBytes(StandardCharsets.UTF_8);

        try (InputStream stream = testResource()
                .resourcePath("/other")
                .resourceName("file")
                .asStream()) {
            assertThat(stream).isNotNull();
            byte[] streamBytes = IOUtils.toByteArray(stream);

            assertThat(streamBytes).containsExactly(expectedBytes);
        }
    }

    @Test
    public void relativeResourceAsString() throws ClassNotFoundException, IOException {
        String testString = testResource()
                .resourceName("relative")
                .asString("UTF-8");

        assertThat(testString).isEqualTo("relative");
    }
}