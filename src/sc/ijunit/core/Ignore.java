package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark test cases that should be ignored.
 *
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Ignore {
}
