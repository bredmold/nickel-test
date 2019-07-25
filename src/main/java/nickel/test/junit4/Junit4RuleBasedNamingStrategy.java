package nickel.test.junit4;

import nickel.test.strategy.ResourceNamingStrategy;
import org.junit.runner.Description;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Naming strategy that uses the test description provided to a rule
 */
public class Junit4RuleBasedNamingStrategy implements ResourceNamingStrategy {
    private final Description description;

    Junit4RuleBasedNamingStrategy(Description description) {
        this.description = requireNonNull(description, "description");
    }

    @Override
    public Optional<String> testName() {
        return Optional.of(description.getMethodName());
    }

    @Override
    public String suiteName(boolean fullyQualified) {
        return fullyQualified
            ? description.getTestClass().getName()
            : description.getTestClass().getSimpleName();
    }
}
