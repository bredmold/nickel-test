package nickel.test.strategy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

public class StackSearchStrategy {
    /**
     * Search the stack to find methods that carry the given annotations.
     *
     * @param annotationClasses A list of annotation classes to search for
     * @return An {@link Optional} referring to the method, possibly empty
     */
    public static Optional<Pair<Method, Annotation>> locateAnnotatedMethod(
        Collection<Class<? extends Annotation>> annotationClasses) {
        if (CollectionUtils.isEmpty(annotationClasses)) {
            throw new IllegalArgumentException("Must provide annotationClasses to search for");
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            Class<?> stackClass;
            Method candidateMethod;
            try {
                stackClass = Class.forName(className);
                candidateMethod = stackClass.getDeclaredMethod(methodName);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                continue;
            }

            for (Class<? extends Annotation> annotationClass : annotationClasses) {
                Annotation annotation = candidateMethod.getAnnotation(annotationClass);
                if (annotation != null) {
                    return Optional.of(ImmutablePair.of(candidateMethod, annotation));
                }
            }
        }

        return Optional.empty();

    }
}
