package nickel.test.junit4;

import com.google.common.collect.ImmutableList;
import nickel.test.strategy.ResourceNamingStrategy;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Naming strategy that uses the test description provided to a rule
 */
public class JUnit4RuleBasedNamingStrategy implements ResourceNamingStrategy {
    static List<Class<? extends Annotation>> JUNIT4_METHOD_ANNOTATIONS = ImmutableList.of(
        Test.class,
        Before.class,
        BeforeClass.class);

    private final Description description;

    JUnit4RuleBasedNamingStrategy(Description description) {
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
