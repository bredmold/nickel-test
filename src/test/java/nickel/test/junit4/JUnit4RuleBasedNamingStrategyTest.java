package nickel.test.junit4;

import org.junit.Test;
import org.junit.runner.Description;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnit4RuleBasedNamingStrategyTest {
    @Test
    public void testName() {
        Description description = Description.createTestDescription(getClass(), "testName");
        JUnit4RuleBasedNamingStrategy namingStrategy = new JUnit4RuleBasedNamingStrategy(description);

        assertThat(namingStrategy.testName())
            .isPresent();
        assertThat(namingStrategy.testName())
            .get()
            .isEqualTo("testName");
    }

    @Test
    public void suiteName() {
        Description description = Description.createTestDescription(getClass(), "suiteName");
        JUnit4RuleBasedNamingStrategy namingStrategy = new JUnit4RuleBasedNamingStrategy(description);

        assertThat(namingStrategy.suiteName(false))
            .isEqualTo("JUnit4RuleBasedNamingStrategyTest");
        assertThat(namingStrategy.suiteName(true))
            .isEqualTo("nickel.test.junit4.JUnit4RuleBasedNamingStrategyTest");
    }
}
