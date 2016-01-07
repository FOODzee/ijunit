package sc.ijunit.core;

/**
 * Assert expressions to be used in {@code @Test}-annotated methods.
 *
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

    /**
     * Asserts that {@code left} is equal to {@code right}.
     */
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
                    "expected to be equal." + getMsg();
        }
    }

    /**
     * Asserts that {@code right} is assignable to {@code left},
     * i.e. code {@code left = right} is correct.
     */
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
                    "left=`" + left.getClass().getCanonicalName() + "`." + getMsg();
        }
    }

    static class Assert extends Error {
        private String msg;

        Assert(String ... msg){
            StringBuilder sb = new StringBuilder();
            for (String s : msg) sb.append(s);
            this.msg = sb.toString();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " failed." + getMsg();
        }

        protected String getMsg() {
            return !msg.isEmpty() ? " There was message: `" + msg + "`" : "";
        }
    }
}
