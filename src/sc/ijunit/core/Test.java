package sc.ijunit.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark test cases.
 *
 * @author foodzee.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Test {
    /**
     * Array of exceptions that current test case expected to throw.
     */
    Class<? extends Throwable>[] expectedExceptions() default DEFAULT.class;

    /**
     * If set, one of expected exceptions must be thrown during testing this case.
     */
    boolean strictExpectations() default false;

    final class DEFAULT extends Throwable {}
}
