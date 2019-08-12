package nickel.test.junit5;

import nickel.test.annotations.NickelTestResource;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(NickelTestExtension.class)
class NickelTestExtensionTest {
    @NickelTestResource
    private String stringValue;

    @Test
    void validateFieldInjection() {
        assertThat(stringValue)
            .isEqualTo("value\n");
    }

    @Test
    void validateParameterInjection(@NickelTestResource(resourceName = "stringValue") InputStream streamValue) throws IOException {
        try {
            byte[] buffer = new byte[100];
            int count = streamValue.read(buffer);
            String value = new String(buffer, 0, count, StandardCharsets.UTF_8);
            assertThat(value)
                .isEqualTo("value\n");
        } finally {
            streamValue.close();
        }
    }
}
