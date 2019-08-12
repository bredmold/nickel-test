package nickel.test.junit5;

import nickel.test.annotations.NickelTestResource;
import nickel.test.inject.NickelTestInjector;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;

/**
 * Test extension that allows for injection of annotated fields in the test class, or on individual test methods.
 */
public class NickelTestExtension implements Extension, BeforeTestExecutionCallback, ParameterResolver {
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        Object testInstance = extensionContext.getTestInstance()
            .orElseThrow(() -> new IllegalStateException("No test instance"));
        NickelTestInjector injector = new NickelTestInjector(testInstance);
        injector.injectAllFields();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(NickelTestResource.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {
        NickelTestResource nickelTestResource = parameterContext.findAnnotation(NickelTestResource.class)
            .orElseThrow(() -> new IllegalStateException(String.format(
                "Unable to locate @NickelTestResource annotation for %s",
                parameterContext.getParameter().getName())));

        Object testInstance = extensionContext.getTestInstance()
            .orElseThrow(() -> new IllegalStateException("No test instance"));
        NickelTestInjector injector = new NickelTestInjector(testInstance);

        Parameter parameter = parameterContext.getParameter();
        return injector.loadResource(nickelTestResource, parameter.getParameterizedType(), parameter.getName());
    }
}
