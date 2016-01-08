package sc.ijunit.tests;

import sc.ijunit.core.Before;
import sc.ijunit.core.BeforeEach;
import sc.ijunit.core.Ignore;
import sc.ijunit.core.Test;

import static sc.ijunit.core.Asserts.*;

/**
 * Negative self-test.
 * Some should fail and fail in a proper way.
 *
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class SelfTestsNegative {

    private int counter;

    @Before
    public void setCounter() {
        counter = 1;
    }

    @BeforeEach
    public void checkCounter() throws Throwable {
        if (counter > 7) throw new Throwable("counter > 7");
        counter++;
    }

    @Test
    public static void checkAsserts() {
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
        throw new Error("Unexpected error",
                new Throwable("That's the cause of error",
                        new Exception("That's the cause of throwable")));
    }

    @Ignore
    public void ignore() {
        System.err.println("You supposed to ignore me!");
    }

    @Test
    public void zzz1() {}

    @Test
    public void zzz2() {}
}
