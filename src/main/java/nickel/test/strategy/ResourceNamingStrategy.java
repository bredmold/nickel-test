package nickel.test.strategy;

import java.util.Optional;

/**
 * This is a strategy for discovering test meta-data.
 */
public interface ResourceNamingStrategy {
    /**
     * Get the name of the current test being run (usually the name of the test method)
     *
     * @return An optional that gives the name of the current test (returns empty if the there is no test)
     */
    Optional<String> testName();

    /**
     * Get the name of the currently executing test suite (usually the name of the test class)
     *
     * @param fullyQualified If true, return the class name with package, otherwise, class name without package
     */
    String suiteName(boolean fullyQualified);
}
