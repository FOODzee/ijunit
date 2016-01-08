package sc.ijunit.tests;

import sc.ijunit.core.After;
import sc.ijunit.core.AfterEach;
import sc.ijunit.core.Test;

/**
 * This test suite supposed to fail after each test and in the end of all.
 * @author foodzee.
 */
@SuppressWarnings("unused")
public class AfterNegative {
    @Test
    public void test1() {}
    @Test
    public void test2() {}
    @Test
    public void test3() {}

    @AfterEach
    public void each() { throw new Error("Fail after each test"); }

    @After
    public void after(){ throw new Error("Fail after all tests"); }
}
