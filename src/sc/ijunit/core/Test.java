package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable>[] expectedException() default DEFAULT.class;

    final class DEFAULT extends Throwable {}
}
