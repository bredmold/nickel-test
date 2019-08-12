package nickel.test.strategy;

import nickel.test.junit4.JUnit4StackBasedNamingStrategy;
import nickel.test.junit5.JUnit5StackBasedNamingStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StackBasedNamingStrategyTest {

    @Test
    void stackBasedStrategy() {
        assertThat(StackBasedNamingStrategy.stackBasedStrategy(StackBasedNamingStrategy.TestFramework.junit5))
            .isInstanceOf(JUnit5StackBasedNamingStrategy.class);

        assertThat(StackBasedNamingStrategy.stackBasedStrategy(StackBasedNamingStrategy.TestFramework.junit4))
            .isInstanceOf(JUnit4StackBasedNamingStrategy.class);
    }
}
