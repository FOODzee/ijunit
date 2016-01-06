package sc.ijunit.tests;

import sc.ijunit.core.*;

import static sc.ijunit.core.Asserts.*;

/**
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class TestBundle1 {

    @Test
    void checkAsserts() {
        // Positive checks
        assertTrue(true);
        assertFalse(false);
        assertEquals("str", "str");
    }

    @Test(expectedException = AssertTrue.class)
    void checkAssertTrueFailure() {
        assertTrue(false);
    }

    @Test(expectedException = AssertFalse.class)
    void checkAssertFalseFailure() {
        assertFalse(true);
    }

    @Test(expectedException = AssertEquals.class)
    void checkAssertEqualsFailure() {
        assertEquals("left", "right");
    }
}
