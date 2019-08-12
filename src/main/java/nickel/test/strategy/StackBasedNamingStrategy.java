package nickel.test.strategy;

import com.google.common.collect.ImmutableMap;
import nickel.test.NickelTestException;
import nickel.test.junit4.JUnit4StackBasedNamingStrategy;
import nickel.test.junit5.JUnit5StackBasedNamingStrategy;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static nickel.test.strategy.StackSearchStrategy.locateAnnotatedMethod;
import static org.apache.commons.lang3.ClassUtils.convertClassNamesToClasses;

/**
 * Base class for stack-based naming strategy implementations
 */
public abstract class StackBasedNamingStrategy implements ResourceNamingStrategy {
    enum TestFramework {junit4, junit5}

    private static final String JUNIT4_TEST = "org.junit.Test";
    private static final String JUNIT4_BEFORE = "org.junit.Before";
    private static final String JUNIT4_BEFORE_CLASS = "org.junit.BeforeClass";
    private static final String JUNIT5_TEST = "org.junit.jupiter.api.Test";
    private static final String JUNIT5_BEFORE_EACH = "org.junit.jupiter.api.BeforeEach";
    private static final String JUNIT5_BEFORE_ALL = "org.junit.jupiter.api.BeforeAll";

    private static final Map<String, TestFramework> FRAMEWORK_ANNOTATIONS =
        ImmutableMap.<String, TestFramework>builder()
            .put(JUNIT4_TEST, TestFramework.junit4)
            .put(JUNIT4_BEFORE, TestFramework.junit4)
            .put(JUNIT4_BEFORE_CLASS, TestFramework.junit4)
            .put(JUNIT5_TEST, TestFramework.junit5)
            .put(JUNIT5_BEFORE_EACH, TestFramework.junit5)
            .put(JUNIT5_BEFORE_ALL, TestFramework.junit5)
            .build();

    /**
     * Constructor method for stack-based naming strategies. Search for specific classes in the classpath to
     * determine which framework to use.
     * <p>
     * If JUnit 5 is present, it will be used.
     * If JUnit 5 is missing, but JUnit 4 is present, then JUnit 4 will be used
     * If neither framework is located, then throw {@link NickelTestException}
     */
    public static ResourceNamingStrategy stackBasedStrategy() {
        List<String> frameworkAnnotationKeys = new ArrayList<>(FRAMEWORK_ANNOTATIONS.keySet());

        @SuppressWarnings("unchecked")
        Map<String, Class<? extends Annotation>> frameworkAnnotationClasses =
            convertClassNamesToClasses(frameworkAnnotationKeys)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Class::getName, c -> (Class<? extends Annotation>) c));

        boolean haveJunit4TestAnnotation = frameworkAnnotationClasses.containsKey(JUNIT4_TEST);
        boolean haveJunit5TestAnnotation = frameworkAnnotationClasses.containsKey(JUNIT5_TEST);

        if (haveJunit5TestAnnotation && haveJunit4TestAnnotation) {
            // Search for ancestor annotations
            Optional<TestFramework> testFramework = locateAnnotatedMethod(frameworkAnnotationClasses.values())
                .map(Pair::getValue)
                .map(Annotation::annotationType)
                .map(Class::getName)
                .map(FRAMEWORK_ANNOTATIONS::get);

            return testFramework
                .map(StackBasedNamingStrategy::stackBasedStrategy)
                .orElseGet(() -> stackBasedStrategy(TestFramework.junit5));
        } else if (haveJunit5TestAnnotation) {
            return stackBasedStrategy(TestFramework.junit5);
        } else if (haveJunit4TestAnnotation) {
            return stackBasedStrategy(TestFramework.junit4);
        } else {
            throw new NickelTestException("Unable to located a supported test framework on the classpath");
        }
    }

    /**
     * Return a stack-based naming strategy for the selected testing framework
     *
     * @param framework The framework to use
     */
    public static ResourceNamingStrategy stackBasedStrategy(TestFramework framework) {
        switch (framework) {
            case junit4:
                return new JUnit4StackBasedNamingStrategy();
            case junit5:
                return new JUnit5StackBasedNamingStrategy();
            default:
                throw new NickelTestException("Unknown test framework: " + framework);
        }
    }
}
