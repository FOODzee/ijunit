package sc.ijunit.core;

/**
 * @author foodzee.
 */
public class Asserts {

    public static void assertTrue(boolean condition, String ... msg) {
        if (!condition)
            throw new AssertTrue(msg);
    }
    static class AssertTrue extends Assert {
        AssertTrue(String ... msg) {super(msg);}
    }

    public static void assertFalse(boolean condition, String ... msg) {
        if (condition)
            throw new AssertFalse(msg);
    }
    static class AssertFalse extends Assert {
        AssertFalse(String ... msg) {super(msg);}
    }

    public static void assertEquals(Object left, Object right, String ... msg) {
        if (!left.equals(right))
            throw new AssertEquals(left, right, msg);
    }
    static class AssertEquals extends Assert {
        private final Object left;
        private final Object right;

        AssertEquals(Object left, Object right, String ... msg) {
            super(msg);
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "AssertEquals failed: " +
                    "left=`" + left + "` " +
                    "and right=`" + right + "` " +
                    "expected to be equal.";
        }
    }

    public static void assertAssignable(Object left, Object right, String ... msg) {
        if (!left.getClass().isAssignableFrom(right.getClass()))
            throw new AssertAssignable (left, right, msg);
    }
    static class AssertAssignable extends Assert {
        private final Object left;
        private final Object right;

        AssertAssignable(Object left, Object right, String ... msg) {
            super(msg);
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "AssertAssignable failed: " +
                    "right=`" + right.getClass().getCanonicalName() + "` " +
                    "expected to be assignable to " +
                    "left=`" + left.getClass().getCanonicalName() + "`.";
        }
    }

    static class Assert extends Error {
        String msg;

        Assert(String ... msg){
            if (msg.length != 0) {
                this.msg = msg[0];
            }
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " failed.";
        }
    }
}
