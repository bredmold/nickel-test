package nickel.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class member with this, and the test will automatically populate it, based on name and type.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NickelTestResource {
    enum Binding {
        /**
         * No object binding
         */
        none,

        /**
         * Jackson v2.x object binding for JSON resources
         */
        jackson,

        /**
         * JAXB binding for XML resources
         */
        jaxb,

        /**
         * YAML (SnakeYaml) objecting binding for YAML resources
         */
        yaml,
    }

    /**
     * The resource name. If this is not provided, it defaults to the name of the field, or method parameter, to which
     * the annotation is attached.
     */
    String resourceName() default "";

    /**
     * The filename extension to use for this resource, if any. This may be provided as part of the resource name.
     * If the resource is bound via a framework (e.g. Jackson), then an extension appropriate to the file format
     * (e.g. ".json") will be presumed.
     */
    String resourceExtension() default "";

    /**
     * The path to the resource, not including the resource itself. If this is provided, it overrides the setting of the
     * {@code includeFullPath} parameter.
     */
    String resourcePath() default "";

    /**
     * For resource types that need it, the String encoding to use in interpreting them
     */
    String encoding() default "UTF-8";

    /**
     * If the path is inferred from the class name (default behavior), this controls whether that path is built on the
     * full class name or the "simple" class name (without the package). The default is to use the simple class name.
     */
    boolean includeFullPath() default false;

    /**
     * Use an object binding framework to map from bytes to objects
     */
    Binding with() default Binding.none;
}
