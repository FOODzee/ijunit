package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Test {
    Class<? extends Throwable>[] expectedExceptions() default DEFAULT.class;
    boolean strictExpectations() default false;

    final class DEFAULT extends Throwable {}
}
