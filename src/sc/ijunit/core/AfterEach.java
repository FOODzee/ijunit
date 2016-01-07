package sc.ijunit.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AfterEach {
}
