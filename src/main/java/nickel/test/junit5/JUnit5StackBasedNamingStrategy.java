package nickel.test.junit5;

import com.google.common.collect.ImmutableList;
import nickel.test.NickelTestException;
import nickel.test.strategy.StackBasedNamingStrategy;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static nickel.test.strategy.StackSearchStrategy.locateAnnotatedMethod;

public class JUnit5StackBasedNamingStrategy extends StackBasedNamingStrategy {
    private static List<Class<? extends Annotation>> JUNIT5_METHOD_ANNOTATIONS = ImmutableList.of(
        Test.class,
        BeforeEach.class,
        BeforeAll.class);

    @Override
    public Optional<String> testName() {
        Optional<Pair<Method, Annotation>> testMethod = locateAnnotatedMethod(singleton(Test.class));
        return testMethod
            .map(Pair::getLeft)
            .map(Method::getName);
    }

    @Override
    public String suiteName(boolean fullyQualified) {
        Optional<Pair<Method, Annotation>> ancestorMethod = locateAnnotatedMethod(JUNIT5_METHOD_ANNOTATIONS);

        Class<?> ancestorClass = ancestorMethod
            .orElseThrow(() -> new NickelTestException("Unable to locate a method with @Test, @BeforeEach, or @BeforeAll"))
            .getLeft()
            .getDeclaringClass();
        return fullyQualified
            ? ancestorClass.getName()
            : ancestorClass.getSimpleName();
    }
}
