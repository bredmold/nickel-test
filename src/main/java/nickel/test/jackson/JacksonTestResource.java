package nickel.test.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nickel.test.NickelTestResource;

import java.io.IOException;

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
        return new JacksonTestResource();
    }

    private ObjectMapper mapper;

    private JacksonTestResource() throws ClassNotFoundException {
        super();
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
        return mapper.readerFor(targetClass)
            .readValue(resolveStream());
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
        return mapper.readerFor(targetType)
            .readValue(resolveStream());
    }
}
