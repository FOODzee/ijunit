package sc.ijunit.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark method, that should be executed after each test case.
 *
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AfterEach {
}
