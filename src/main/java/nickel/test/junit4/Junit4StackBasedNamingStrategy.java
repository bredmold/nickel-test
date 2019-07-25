package nickel.test.junit4;

import nickel.test.NickelTestException;
import nickel.test.strategy.ResourceNamingStrategy;
import org.apache.commons.lang3.ClassUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Examine the call stack and looks for ancestor method annotated with {@link Test} or {@link Before} in order to
 * determine the identity of the current test.
 */
public class Junit4StackBasedNamingStrategy implements ResourceNamingStrategy {
    @Override
    public Optional<String> testName() {
        Optional<Method> testMethod = locateAnnotatedMethod(Test.class);
        return testMethod.map(Method::getName);
    }

    @Override
    public String suiteName(boolean fullyQualified) {
        Optional<Method> ancestorMethod = locateAnnotatedMethod(Test.class);
        if (!ancestorMethod.isPresent()) {
            ancestorMethod = locateAnnotatedMethod(Before.class);
        }

        Class<?> ancestorClass = ancestorMethod
            .orElseThrow(() -> new NickelTestException("Unable to locate a method with @Test or @Before"))
            .getDeclaringClass();
        return fullyQualified
            ? ancestorClass.getName()
            : ancestorClass.getSimpleName();
    }

    /**
     * Examine the current thread's stack to find a target method. A target method is defined as follows:
     * <ol>
     * <li>Empty argument list</li>
     * <li>Annotated with the selected annotation</li>
     * </ol>
     *
     * @param annotationClass The annotation class we're searching for
     * @return {@link Method} reference, if the method is found, otherwise null
     */
    private static <A extends Annotation> Optional<Method> locateAnnotatedMethod(Class<A> annotationClass) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            Class<?> stackClass;
            Method candidateMethod;
            try {
                stackClass = Class.forName(className);
                candidateMethod = ClassUtils.getPublicMethod(stackClass, methodName);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                continue;
            }

            A targetAnnotation = candidateMethod.getAnnotation(annotationClass);
            if (targetAnnotation != null) {
                return Optional.of(candidateMethod);
            }
        }

        return Optional.empty();
    }
}
