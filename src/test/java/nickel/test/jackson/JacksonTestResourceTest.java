package nickel.test.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nickel.test.BeanClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static nickel.test.jackson.JacksonTestResource.jacksonTestResource;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTestResourceTest {
    @Test
    public void jsonResourceForClass() throws ClassNotFoundException, IOException {
        BeanClass instance = jacksonTestResource()
            .forTestMethod()
            .asJson(BeanClass.class);

        assertThat(instance).isNotNull();
        assertThat(instance).hasFieldOrPropertyWithValue("value", 42);
    }

    @Test
    public void jsonResourceForType() throws ClassNotFoundException, IOException {
        List<BeanClass> list = jacksonTestResource()
            .forTestMethod()
            .asJson(new TypeReference<List<BeanClass>>() {
            });

        assertThat(list).isNotNull();
        assertThat(list).hasSize(2);
        assertThat(list).containsExactly(
            new BeanClass(1),
            new BeanClass(2));
    }

    @Test
    public void jsonResourceWithMapper() throws ClassNotFoundException, IOException {
        Instant expected = LocalDateTime.of(2019, 1, 1, 0, 0, 0, 0)
            .toInstant(ZoneOffset.UTC);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Instant instant = jacksonTestResource()
            .withMapper(mapper)
            .forTestMethod()
            .asJson(Instant.class);

        assertThat(instant).isEqualTo(expected);
    }
}
