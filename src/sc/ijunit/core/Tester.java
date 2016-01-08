package sc.ijunit.core;

import sc.ijunit.core.Asserts.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Thread responsible to testing classes from main job list.
 * See {@link Main#jobs}.
 *
 * @author foodzee.
 */
final class Tester extends Thread {
    private Class job;
    private int failCount;

    @Override
    public void run() {
        while (!interrupted()) {
            try   { test(Main.jobs.remove(0)); }
            catch ( ArrayIndexOutOfBoundsException e )
            { /* Give main thread some time to add new jobs or interrupt us. */ }
        }
    }

    /**
     * Performs all tests in given class.
     */
    private void test(Class job) {
        this.job = job;
        log("Testing of " + job + " started.");

        // Find methods to test and to execute before/after tests
        ArrayList<Method> tests = new ArrayList<>();
        Method before = null, beforeEach = null;
        Method after  = null, afterEach  = null;
        for (Method m : job.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BeforeEach.class)) beforeEach = m;
            if (m.isAnnotationPresent(AfterEach.class))  afterEach  = m;
            if (m.isAnnotationPresent(Before.class))     before = m;
            if (m.isAnnotationPresent(After.class))      after  = m;
            if (m.isAnnotationPresent(Test.class) ||
                m.isAnnotationPresent(Ignore.class))     tests.add(m);
        }

        // Instantiate test class
        Object jObj;
        try {
            jObj = job.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            constrFailure(e);
            log("Testing of " + job + " failed. See details above.\n");
            return;
        }

        // Prepare test class
        if (!invoke(before, jObj, "error while preparing test class")) {
            log("Testing of " + job + " failed. See details above.\n");
            return;
        }

        failCount = 0;
        int skipCount = 0;
        int ignoreCount = 0;
        int finCount = 0;

        for (Method t : tests) {
            // Should we ignore this test?
            if (t.isAnnotationPresent(Ignore.class)) {
                log("Test `" + t.getName() + "` ignored");
                ignoreCount++;
                continue;
            }

            // Prepare to perform the test
            if (!invoke(beforeEach, jObj, "error while preparing to perform test " + t.getName() + ":")) {
                // If `beforeEach` fails we can not execute this test
                log("Test `" + t.getName() + "` skipped due to preparation error. See details above.");
                skipCount++;
                continue;
            }

            final Test annot = t.getDeclaredAnnotation(Test.class);
            final Class<?>[] expectedExceptions = annot.expectedExceptions();

            // Try to pass the test
            try {
                t.invoke(jObj);

                if (annot.strictExpectations() && expectedExceptions.length != 0) {
                    // We strongly hoped our test to throw something expected, but it doesn't
                    StringBuilder sb = new StringBuilder();
                    for (Class<?> expected : expectedExceptions) {
                        sb.append("\n\t").append(expected.getCanonicalName());
                    }

                    testFailure(t, "one of the following exceptions expected, but non occurred:" + sb, null);
                } else {
                    // Everything worked ok, test passed
                    log("Test `" + t.getName() + "` passed");
                }
            } catch (InvocationTargetException it) {
                // Something went wrong...
                final Throwable e = it.getTargetException();

                // Check whether we expected these exception
                for (Class<?> expected : expectedExceptions) {
                    if (expected.isAssignableFrom(e.getClass())) {
                        // ...but not too wrong, test expected such behaviour
                        log("Test `" + t.getName() + "` passed");
                        break;
                    } else if (e instanceof Assert) {
                        testFailure(t, "assertion haven't passed", e);
                    } else {
                        testFailure(t, "unexpected exception has been thrown:", e);
                    }
                }
            } catch (IllegalAccessException ia) {
                testFailure(t, "illegal access", ia);
            }

            // Test ended, do something if needed.
            if (!invoke(afterEach, jObj, "error while finalizing test case " + t.getName())) finCount++;
        }

        // No more test to do, finalize test class
        boolean afterFailed = !invoke(after, jObj, "error while finalizing test class");

        // Write out how good (or bad) everything went
        boolean errors = (failCount > 0) || (skipCount > 0) || (ignoreCount > 0) || (finCount > 0) || afterFailed;
        synchronized (System.out) { // Safely output several strings.
            log("Testing of " + job + " finished " + (!errors ? "successful." : "with"));
            outProblem(failCount,   tests.size(), " failed.");
            outProblem(skipCount,   tests.size(), " skipped.");
            outProblem(ignoreCount, tests.size(), " ignored.");
            outProblem(finCount,    tests.size(), " caused problems during finalization.");
            if (afterFailed) log ("problems during finalization of test class.");
            log("");
        }
    }

    /**
     * Reports how much tests ended with specific state.
     * Reports nothing if {@code count = 0}.
     *
     * @param count Number to report
     * @param testsNumber Overall amount of tests
     * @param state End state
     */
    private void outProblem(int count, int testsNumber, String state) {
        if (count == 1)
            log("one test of " + testsNumber + state);
        else if (count > 1)
            log(count + " tests of " + testsNumber + state);
    }

    /**
     * Invokes reflective method `m` of object `jObj`.
     * If invocation caused no errors returns {@code true},
     * otherwise reports failure and returns {@code false}.
     *
     * @param m    Method to invoke
     * @param jObj Object of class this method belongs to
     * @param msg  String to put to log
     *
     * @return     {@code true} if invocation succeeded (or there was no one)
     *             {@code false} if there was a failure.
     */
    private boolean invoke(Method m, Object jObj, String msg) {
        if (m != null) try {
            m.invoke(jObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            methodFailure(m, msg, e);
            return false;
        }
        return true;
    }

    private void methodFailure(Method m, String msg, Throwable th) {
        failure("method `" + m.getName() + "` in class ", msg, th);
    }

    private void constrFailure(Throwable th) {
        failure("instantiating of class ", "", th);
    }

    private void testFailure(Method test, String msg, Throwable th) {
        failure("test `" + test.getName() + "` in class ", msg, th);
        failCount++;
    }

    private void failure(String s, String msg, Throwable th) {
        synchronized (System.out) { // Safely output several strings.
            log("/-------");
            log(s + job.getCanonicalName() + " failed :");
            log(msg);
            if (th != null) { // Write out given Throwable with all causes.
                log(th.toString());
                while ((th = th.getCause()) != null) {
                    log("caused by " + th.toString());
                }
            }
            log("\\-------");
        }
    }

    private void log(String msg) {
        System.out.printf(getId() + ": " + msg + "\n");
    }
}
