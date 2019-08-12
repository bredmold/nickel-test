package nickel.test.strategy;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StackSearchStrategyTest {
    @Test
    void locateAnnotatedMethod() {
        assertThrows(
            IllegalArgumentException.class,
            () -> StackSearchStrategy.locateAnnotatedMethod(emptyList()),
            "Must provide annotationClasses to search for");
    }
}
