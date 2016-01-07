package sc.ijunit.core;

import sc.ijunit.core.Asserts.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author foodzee.
 */
public class Tester extends Thread {
    private Class job;
    private boolean errors;
    private int failCounter;

    @Override
    public void run() {
        while (!interrupted()) {
            try   { test(Main.jobs.remove(0)); }
            catch ( ArrayIndexOutOfBoundsException e )
            { /* Give main thread some time to add new jobs or interrupt us. */ }
        }
    }

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
            if (m.isAnnotationPresent(Test.class))       tests.add(m);
        }

        // Instantiate test class
        Object jObj;
        try {
            jObj = job.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            constrFailure(e);
            log("Testing of " + job + " failed.");
            return;
        }

        // Prepare test class
        if (!invoke(before, jObj, "error while preparing test class")) {
            log("Testing of " + job + " failed.");
            return;
        }

        errors = false;
        failCounter = 0;
        int skipCounter = 0;
        int ignoreCounter = 0;

        for (Method t : tests) {
            // Should we ignore this test?
            if (t.isAnnotationPresent(Ignore.class)) {
                errors = true;
                ignoreCounter++;
                log("Test `" + t.getName() + "` ignored");
                continue;
            }

            // Try to execute `@BeforeEach`-marked method
            if (!invoke(beforeEach, jObj, "error while preparing to perform test " + t.getName())) {
                // If it fails we can not execute this test
                errors = true;
                skipCounter++;
                log("Test `" + t.getName() + "` skipped");
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

            invoke(afterEach, jObj, "error while finalizing test case " + t.getName());
        }

        // No more test to do, finalize test class
        invoke(after, jObj, "error while finalizing test class");

        // Write out how good everything went
        synchronized (System.out) { // Safely output several strings.
            log("Testing of " + job + " finished " + (!errors ? "successful." : "with"));
            outProblem(failCounter,   tests.size(), " failed.");
            outProblem(skipCounter,   tests.size(), " skipped.");
            outProblem(ignoreCounter, tests.size(), " ignored.");
        }
    }

    private boolean invoke(Method m, Object jObj, String msg) {
        if (m != null) try {
            m.invoke(jObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            methodFailure(m, msg, e);
            return false;
        }
        return true;
    }

    private void constrFailure(Throwable th) {
        failure("instantiating of class ", "", th);
    }

    private void testFailure(Method test, String msg, Throwable th) {
        failure("test `" + test.getName() + "` in class ", msg, th);
        failCounter++;
        errors = true;
    }

    private void methodFailure(Method m, String msg, Throwable th) {
        failure("method `" + m.getName() + "` in class ", msg, th);
    }

    private void failure(String s, String msg, Throwable th) {
        synchronized (System.out) { // Safely output several strings.
            log("/-------");
            log(s + job.getCanonicalName() + " failed :");
            log(msg);
            if (th != null) log(th.toString());
            log("\\-------");
        }
    }

    private void outProblem(int counter, int testsNumber, String state) {
        if (counter == 1)
            log("one test of " + testsNumber + state);
        else if (counter > 1)
            log(counter + " tests of " + testsNumber + state);
    }

    private void log(String msg) {
        System.out.printf(getId() + ": " + msg + "\n");
    }
}
