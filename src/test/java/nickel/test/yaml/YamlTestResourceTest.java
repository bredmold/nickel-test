package nickel.test.yaml;

import nickel.test.BeanClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

import static nickel.test.yaml.YamlTestResource.yamlTestResource;
import static org.assertj.core.api.Assertions.assertThat;

public class YamlTestResourceTest {
    @Test
    public void yamlResource() throws ClassNotFoundException, IOException {
        BeanClass beanClass = yamlTestResource()
            .forTestMethod()
            .asYaml(BeanClass.class);

        assertThat(beanClass).isEqualTo(new BeanClass(208));
    }

    @Test
    public void customYaml() throws ClassNotFoundException, IOException {
        BeanClass beanClass = yamlTestResource()
            .forTestMethod()
            .withYaml(new Yaml())
            .asYaml(BeanClass.class);

        assertThat(beanClass).isEqualTo(new BeanClass(405));
    }
}
