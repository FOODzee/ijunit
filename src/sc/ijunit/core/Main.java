package sc.ijunit.core;

import java.util.Vector;

public class Main {

    /**
     * All the classes to be tested.
     * {@link Tester}s will get theirs jobs from here.
     */
    static volatile Vector<Class> jobs;

    /**
     * Entry point of testing system.
     * Arguments must be specified in the following way:
     * <ul>
     * <li> 1) number of threads to occupy by testers;  </li>
     * <li> 2) first class with @Test annotated methods;</li>
     * <li> ...                                         </li>
     * <li> n) last class with @Test annotated methods. </li>
     * </ul>
     */
    public static void main(String[] args) {
        final int numberOfJobs = args.length - 1;
        if (numberOfJobs < 1) {
            usage();
        }
        jobs = new Vector<>(numberOfJobs);

        int numberOfThreads = 0;
        try {
            numberOfThreads = Integer.decode(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("First parameter must be an integer specifying number of threads to use.");
            usage();
        }

        if (numberOfThreads == 0) {
            System.out.println("You specified zero threads to use, that makes no sense.");
            usage();
        } else if (numberOfThreads > Runtime.getRuntime().availableProcessors()) {
            System.out.println("You specified more threads to use than it is available.");
            System.out.println("That makes no sense, but is not critical.");
        }

        Tester[] testers = new Tester[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            testers[i] = new Tester();
            testers[i].start();
        }

        for (int i = 1; i <= numberOfJobs; i++) {
            try {
                jobs.add(Class.forName(args[i]));
            } catch (ClassNotFoundException e) {
                System.out.println(e + " at " + i + " argument");
                interruptAndWait(testers);
                usage();
            }
        }

        while (!jobs.isEmpty()) synchronized (Thread.currentThread()) {
            try   { Thread.currentThread().wait(10); }
            catch ( InterruptedException e )
            { /* Give testers some time to check remaining tests. */ }
        }

        interruptAndWait(testers);
    }

    protected static void interruptAndWait(Tester[] testers) {
        for (Tester t : testers) t.interrupt();

        boolean aliveTesters = true;
        while (aliveTesters) synchronized (Thread.currentThread()) {
            aliveTesters = false;
            for (Tester t : testers) {
                if (t.isAlive()) {
                    aliveTesters = true;
                    break;
                }
            }
        }
    }

    private static void usage() {
        System.out.printf("Usage:\n\tjava -cp ijunit.jar;<tested-classes> sc.ijunit.core.Main N class-name [class-name]*\n");
        System.exit(-1);
    }
}
