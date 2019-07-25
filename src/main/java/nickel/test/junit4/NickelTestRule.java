package nickel.test.junit4;

import nickel.test.NickelTestResource;
import nickel.test.jackson.JacksonTestResource;
import nickel.test.jaxb.JaxbTestResource;
import nickel.test.yaml.YamlTestResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Provide nickel-test functionality as a test rule
 */
public class NickelTestRule implements TestRule {
    private Description description;

    @Override
    public Statement apply(final Statement base, Description description) {
        this.description = description;
        return base;
    }

    /**
     * Return a "generic" test resource that can translate types built in to Java
     */
    NickelTestResource<NickelTestResource> testResource() throws ClassNotFoundException {
        return new NickelTestResource<>(new Junit4RuleBasedNamingStrategy(description));
    }

    /**
     * JSON test resource built using the Jackson library
     */
    JacksonTestResource jacksonTestResource() throws ClassNotFoundException {
        return new JacksonTestResource(new Junit4RuleBasedNamingStrategy(description));
    }

    /**
     * XML test resource bound using JAXB
     */
    JaxbTestResource jaxbTestResource() throws ClassNotFoundException {
        return new JaxbTestResource(new Junit4RuleBasedNamingStrategy(description));
    }

    /**
     * YAML test resource, bound using the SnakeYAML library
     */
    YamlTestResource yamlTestResource() throws ClassNotFoundException {
        return new YamlTestResource(new Junit4RuleBasedNamingStrategy(description));
    }
}
