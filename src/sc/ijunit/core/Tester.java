package sc.ijunit.core;

import sc.ijunit.core.Asserts.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author foodzee.
 */
public class Tester extends Thread {
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
        log("Testing of " + job + " started.");

        ArrayList<Method> tests = new ArrayList<>();
        Method before = null, after = null;
        for (Method m : job.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Before.class)) before = m;
            if (m.isAnnotationPresent(After.class))  after  = m;
            if (m.isAnnotationPresent(Test.class))   tests.add(m);
        }

        Object jObj;
        try {
            jObj = job.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            constrFailure(job, e);
            log("Testing of " + job + " failed.");
            return;
        }

        if (before != null) try {
            before.invoke(jObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            methodFailure(before, job, "error while preparing test class", e);
            log("Testing of " + job + " failed.");
            return;
        }

        failCounter = 0;
        for (Method t : tests) {
            Class<?>[] expectedExceptions = t.getDeclaredAnnotation(Test.class).expectedExceptions();

            try {
                t.invoke(jObj);
                log("Test `" + t.getName() + "` passed");
            } catch (InvocationTargetException it) {
                final Throwable e = it.getTargetException();

                // Check whether we expected these exception.
                for (Class<?> expected : expectedExceptions) {
                    if (expected.isAssignableFrom(e.getClass())) {
                        log("Test `" + t.getName() + "` passed");
                        break;
                    } else if (e instanceof Assert) {
                        testFailure(t, job, "assertion haven't passed", e);
                    } else {
                        testFailure(t, job, "unexpected exception has been thrown:", e);
                    }
                }
            } catch (IllegalAccessException ia) {
                testFailure(t, job, "illegal access", ia);
            }
        }

        if (after != null) try {
            after.invoke(jObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            methodFailure(after, job, "error while finalizing test class", e);
        }

        synchronized (System.out) { // Safely output several strings.
            log("Testing of " + job + " finished " + ((failCounter == 0) ? "successful." : "with"));
            if (failCounter == 1)
                log("one test of " + tests.size() + " failed.");
            else if (failCounter > 1)
                log(failCounter + " tests of " + tests.size() + " failed.");
            System.out.println();
        }
    }

    private void constrFailure(Class job, Throwable th) {
        failure("instantiating of class ", job, "", th);
    }

    private void testFailure(Method test, Class job, String msg, Throwable th) {
        failure("test `" + test.getName() + "` in class ", job, msg, th);
        failCounter++;
    }

    private void methodFailure(Method m, Class job, String msg, Throwable th) {
        failure("method `" + m.getName() + "` in class ", job, msg, th);
    }

    private void failure(String s, Class job, String msg, Throwable th) {
        synchronized (System.out) { // Safely output several strings.
            log("/-------");
            log(s + job.getCanonicalName() + " failed :");
            log(msg);
            log(th.toString());
            log("\\-------");
        }
    }

    private void log(String msg) {
        System.out.println(getId() + ": " + msg);
    }
}
