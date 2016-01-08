package sc.ijunit.tests;

import sc.ijunit.core.Before;

/**
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class BeforeNegative {
    @Before
    public void fail() { throw new Error("Just fail."); }
}
