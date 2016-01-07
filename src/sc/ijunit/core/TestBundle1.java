package sc.ijunit.core;

import static sc.ijunit.core.Asserts.*;

/**
 * Positive self-test.
 * Should all pass.
 *
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class TestBundle1 {

    private Assert ass;
    private AssertEquals assEq;
    private AssertTrue assTrue;

    @Before
    public void before() {
        ass = new Assert();
        assEq = new AssertEquals("", "");
        assTrue = new AssertTrue();
    }

    @Test
    public void checkAsserts() {
        // Positive checks
        assertTrue(true);
        assertFalse(false);
        assertEquals("str", "str");
        assertAssignable(ass, assEq);
    }

    @Test(expectedExceptions = Assert.class)
    public void checkAssertTrueFailure() {
        assertTrue(false);
    }

    @Test(expectedExceptions = AssertFalse.class)
    public void checkAssertFalseFailure() {
        assertFalse(true);
    }

    @Test(expectedExceptions = AssertEquals.class)
    public void checkAssertEqualsFailure() {
        assertEquals("left", "right");
    }

    @Test(expectedExceptions = AssertAssignable.class)
    public void checkWrongAssignment1() {
        assertAssignable(assEq, ass);
    }

    @Test(expectedExceptions = AssertAssignable.class)
    public void checkWrongAssignment2() {
        assertAssignable(assEq, assTrue);
    }

    @Test(expectedExceptions = {AssertAssignable.class, AssertEquals.class, AssertTrue.class},
          strictExpectations = false)
    public static void doNothing() {}
}
