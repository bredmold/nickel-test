package nickel.test.yaml;

import nickel.test.NickelTestResource;
import nickel.test.strategy.ResourceNamingStrategy;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

import static nickel.test.strategy.StackBasedNamingStrategy.stackBasedStrategy;

/**
 * YAML Test resource processed with the SnakeYAML library.
 */
public class YamlTestResource extends NickelTestResource<YamlTestResource> {
    public static YamlTestResource yamlTestResource() throws ClassNotFoundException {
        return new YamlTestResource(stackBasedStrategy());
    }

    private Yaml yaml;

    public YamlTestResource(ResourceNamingStrategy namingStrategy) throws ClassNotFoundException {
        super(namingStrategy);
    }

    /**
     * Use a particular YAML processor instance to parse.
     *
     * @param yaml The YAML processor to use
     */
    public YamlTestResource withYaml(Yaml yaml) {
        this.yaml = yaml;
        return this;
    }

    /**
     * Bind a test resource to a bean class using the YAML processor.
     *
     * @param targetClass The desired type to bind the YAML into
     * @param <T>         The target type
     * @return Bound object from the YAML processor
     */
    public <T> T asYaml(Class<T> targetClass) throws IOException {
        defaultResourceExtension(".yaml");
        try (InputStream stream = resolveStream()) {
            return yaml().loadAs(stream, targetClass);
        }
    }

    private Yaml yaml() {
        return (yaml == null)
            ? new Yaml()
            : yaml;
    }
}
