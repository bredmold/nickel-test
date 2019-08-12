package nickel.test.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import nickel.test.NickelTestResource;
import nickel.test.strategy.ResourceNamingStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import static nickel.test.strategy.StackBasedNamingStrategy.stackBasedStrategy;

/**
 * JSON test resource, to be parsed using Jackson.
 *
 * <p>If no {@link ObjectMapper} is supplied, then a default instance will be used.</p>
 */
public class JacksonTestResource extends NickelTestResource<JacksonTestResource> {
    /**
     * Acquire an instance.
     */
    public static JacksonTestResource jacksonTestResource() throws ClassNotFoundException {
        return new JacksonTestResource(stackBasedStrategy());
    }

    private ObjectMapper mapper;

    public JacksonTestResource(ResourceNamingStrategy namingStrategy) throws ClassNotFoundException {
        super(namingStrategy);
        mapper = new ObjectMapper();
    }

    /**
     * Use the user-specified {@link ObjectMapper} instead of the default.
     *
     * @param mapper The {@link ObjectMapper} instance to use
     */
    public JacksonTestResource withMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Use the {@link ObjectMapper} to render the test resource into an object.
     *
     * <p>If no <code>resourceExtension</code> has been supplied, this will supply a default of <code>.json</code>.</p>
     *
     * @param targetClass What kind of object to create
     * @param <T>         Target type
     * @return Instantiated version of the object
     */
    public <T> T asJson(Class<T> targetClass) throws IOException {
        defaultResourceExtension(".json");
        try (InputStream stream = resolveStream()) {
            return mapper().readerFor(targetClass)
                .readValue(stream);
        }
    }

    /**
     * Use the {@link ObjectMapper} to render the test resource into an object
     *
     * <p>If no <code>resourceExtension</code> has been supplied, this will supply a default of <code>.json</code>.</p>
     *
     * @param targetType What kind of object to create
     * @param <T>        Target type
     * @return Instantiated version of the object
     */
    public <T> T asJson(TypeReference<T> targetType) throws IOException {
        defaultResourceExtension(".json");
        try (InputStream stream = resolveStream()) {
            return mapper().readerFor(targetType)
                .readValue(stream);
        }
    }

    /**
     * Use the {@link ObjectMapper} to render the test resource into an object
     *
     * <p>If no <code>resourceExtension</code> has been supplied, this will supply a default of <code>.json</code>.</p>
     *
     * @param targetType What kind of object to create
     * @param <T>        Target type
     * @return Instantiated version of the object
     */
    public <T> T asJson(Type targetType) throws IOException {
        defaultResourceExtension(".json");
        try (InputStream stream = resolveStream()) {
            JavaType javaType = mapper().constructType(targetType);
            return mapper().readerFor(javaType)
                .readValue(stream);
        }
    }

    private ObjectMapper mapper() {
        return (mapper == null)
            ? new ObjectMapper()
            : mapper;
    }
}
