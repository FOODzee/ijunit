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
    public void checkAsserts() {
        // Positive checks
        assertTrue(true);
        assertFalse(false);
        assertEquals("str", "e", "How can that be? D:");
    }

    @Test
    public void checkAssignable() {
        assertAssignable("", new Assert());
    }

    @Test(expectedExceptions = AssertFalse.class)
    public void checkAssertTrueFailure() {
        assertTrue(false, "Ooops");
    }

    @Test(expectedExceptions = AssertFalse.class)
    public void checkAssertFalseFailure() {
        assertFalse(true);
    }

    @Test(expectedExceptions = AssertEquals.class)
    public void checkAssertEqualsFailure() {
        assertEquals("left", "right");
    }
}
