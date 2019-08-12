package nickel.test.junit5;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JUnit5StackBasedNamingStrategyTest {
    @Test
    void testName() {
        assertThat(new JUnit5StackBasedNamingStrategy().testName())
            .isPresent()
            .get()
            .isEqualTo("testName");
    }

    @Test
    void suiteName() {
        assertThat(new JUnit5StackBasedNamingStrategy().suiteName(false))
            .isEqualTo(getClass().getSimpleName());

        assertThat(new JUnit5StackBasedNamingStrategy().suiteName(true))
            .isEqualTo(getClass().getName());
    }
}
