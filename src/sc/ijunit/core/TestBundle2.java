package sc.ijunit.core;

import static sc.ijunit.core.Asserts.*;

/**
 * Negative self-test.
 * Some should fail and fail in a proper way.
 *
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class TestBundle2 {

    @Test
    public static void checkAsserts() {
        // Positive checks
        assertTrue(true);
        assertFalse(false);
        assertEquals("str", "e", "How can that be? D:");
    }

    @Test
    public static void checkAssignable() {
        assertAssignable("", new Assert());
    }

    @Test(expectedExceptions = AssertFalse.class)
    public static void checkAssertTrueFailure() {
        assertTrue(false, "Ooops");
    }

    @Test(expectedExceptions = AssertFalse.class)
    public static void checkAssertFalseFailure() {
        assertFalse(true);
    }

    @Test(expectedExceptions = AssertEquals.class)
    public static void checkAssertEqualsFailure() {
        assertEquals("left", "right");
    }

    @Test(expectedExceptions = {AssertAssignable.class, AssertEquals.class, AssertTrue.class},
          strictExpectations = true)
    public static void doNothing() {}

    @Test
    public static void unexpected() {
        throw new Error("Unexpected error", new Throwable("That's the cause of error"));
    }
}
