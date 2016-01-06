package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
