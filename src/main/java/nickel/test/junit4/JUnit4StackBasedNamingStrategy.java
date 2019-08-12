package nickel.test.junit4;

import nickel.test.NickelTestException;
import nickel.test.strategy.StackBasedNamingStrategy;
import nickel.test.strategy.StackSearchStrategy;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static nickel.test.junit4.JUnit4RuleBasedNamingStrategy.JUNIT4_METHOD_ANNOTATIONS;

/**
 * Examine the call stack and looks for ancestor method annotated with {@link Test}, {@link Before} or
 * {@link BeforeClass} in order to determine the identity of the current test.
 */
public class JUnit4StackBasedNamingStrategy extends StackBasedNamingStrategy {
    @Override
    public Optional<String> testName() {
        Optional<Pair<Method, Annotation>> testMethod = StackSearchStrategy.locateAnnotatedMethod(singletonList(Test.class));
        return testMethod
            .map(Pair::getLeft)
            .map(Method::getName);
    }

    @Override
    public String suiteName(boolean fullyQualified) {
        Optional<Pair<Method, Annotation>> ancestorMethod = StackSearchStrategy.locateAnnotatedMethod(JUNIT4_METHOD_ANNOTATIONS);

        Class<?> ancestorClass = ancestorMethod
            .orElseThrow(() -> new NickelTestException("Unable to locate a method with @Test, @Before, or @BeforeClass"))
            .getLeft()
            .getDeclaringClass();
        return fullyQualified
            ? ancestorClass.getName()
            : ancestorClass.getSimpleName();
    }
}
