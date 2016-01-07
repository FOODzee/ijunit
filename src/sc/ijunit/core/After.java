package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark method, that should be executed
 * after all test cases in current class are finished.
 *
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface After {
}
