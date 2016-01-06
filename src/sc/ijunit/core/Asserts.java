package sc.ijunit.core;

/**
 * @author foodzee.
 */
public class Asserts {

    public static void assertTrue(boolean condition, String ... msg) {
        if (!condition)
            throw new AssertTrue(msg);
    }
    public static class AssertTrue extends Assert {
        AssertTrue(String ... msg) {super(msg);}
    }

    public static void assertFalse(boolean condition, String ... msg) {
        if (condition)
            throw new AssertFalse(msg);
    }
    public static class AssertFalse extends Assert {
        AssertFalse(String ... msg) {super(msg);}
    }

    public static void assertEquals(Object left, Object right, String ... msg) {
        if (!left.equals(right))
            throw new AssertEquals(msg);
    }
    public static class AssertEquals extends Assert {
        AssertEquals(String ... msg) {super(msg);}
    }

    static class Assert extends Error {
        String msg;

        Assert(String ... msg){
            if (msg.length != 0) {
                this.msg = msg[0];
            }
        }
    }
}
