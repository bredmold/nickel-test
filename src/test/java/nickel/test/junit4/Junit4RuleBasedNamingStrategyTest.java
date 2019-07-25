package nickel.test.junit4;

import org.junit.Test;
import org.junit.runner.Description;

import static org.assertj.core.api.Assertions.assertThat;

public class Junit4RuleBasedNamingStrategyTest {
    @Test
    public void testName() {
        Description description = Description.createTestDescription(getClass(), "testName");
        Junit4RuleBasedNamingStrategy namingStrategy = new Junit4RuleBasedNamingStrategy(description);

        assertThat(namingStrategy.testName())
            .isPresent();
        assertThat(namingStrategy.testName())
            .get()
            .isEqualTo("testName");
    }

    @Test
    public void suiteName() {
        Description description = Description.createTestDescription(getClass(), "suiteName");
        Junit4RuleBasedNamingStrategy namingStrategy = new Junit4RuleBasedNamingStrategy(description);

        assertThat(namingStrategy.suiteName(false))
            .isEqualTo("Junit4RuleBasedNamingStrategyTest");
        assertThat(namingStrategy.suiteName(true))
            .isEqualTo("nickel.test.junit4.Junit4RuleBasedNamingStrategyTest");
    }
}
