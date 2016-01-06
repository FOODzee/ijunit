package sc.ijunit.core;

import sc.ijunit.core.Asserts.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author foodzee.
 */
public class Tester extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            try   { test(Main.jobs.remove(0)); }
            catch ( ArrayIndexOutOfBoundsException e )
            { /* `jobs.remove` may throw IOB if there is no jobs yet */ }
        }
    }

    private void test(Class job) {
        ArrayList<Method> methods = new ArrayList<>();
        Method before = null, after = null;
        for (Method m : job.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Before.class)) before = m;
            if (m.isAnnotationPresent(After.class))  after  = m;
            if (m.isAnnotationPresent(Test.class))   methods.add(m);
        }

        Object jObj = null;
        if (before != null) {
            try {
                jObj = job.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                constrFailure(job, e);
                return;
            }

            try {
                before.invoke(jObj);
            } catch (InvocationTargetException | IllegalAccessException e) {
                methodFailure(before, job, "error while preparing test class", e);
                return;
            }
        }

        for (Method m : methods) {
            Class<? extends Throwable>[] expectedExceptions =
                    m.getDeclaredAnnotation(Test.class).expectedExceptions();

            try {
                m.invoke(jObj);
            } catch (InvocationTargetException it) {
                final Throwable e = it.getTargetException();
                if (e instanceof Assert) {
                    testFailure(m, job, "assertion haven't passed", e);
                } else {
                    for (Class<? extends Throwable> expected : expectedExceptions) {
                        if (! e.getClass().isAssignableFrom(expected)) {
                            testFailure(m, job, "unexpected exception has been thrown:", e);
                        }
                    }
                }
            } catch (IllegalAccessException ia) {
                testFailure(m, job, "illegal access", ia);
            }
        }

        if (after != null) try {
            after.invoke(jObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            methodFailure(after, job, "error while finalizing test class", e);
        }
    }

    private void constrFailure(Class job, Throwable th) {
        failure("instantiating of class ", job, "", th);
    }

    private void testFailure(Method test, Class job, String msg, Throwable th) {
        failure("test " + test + " in class ", job, msg, th);
    }

    private void methodFailure(Method m, Class job, String msg, Throwable th) {
        failure("method " + m + " in class ", job, msg, th);
    }

    private void failure(String s, Class job, String msg, Throwable th) {
        System.out.println(s + job + " failed :");
        System.out.println(msg);
        System.out.println(th);
    }
}
