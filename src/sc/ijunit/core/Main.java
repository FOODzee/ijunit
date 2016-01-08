package sc.ijunit.core;

import java.util.Vector;

public class Main {

    final static Logger logger = new Logger();

    /**
     * All the classes to be tested.
     * {@link Tester}s will get theirs jobs from here.
     */
    static Vector<Class> jobs;

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
        try { // Init logger and remove its arguments
            args = logger.init(args);
        } catch (Logger.InvalidArgumentException e) {
            System.err.println(e.getMessage());
            usage();
        }

        final int numberOfJobs = args.length - 1;
        if (numberOfJobs < 1) {
            usage();
        }
        jobs = new Vector<>(numberOfJobs);

        int numberOfThreads = 0;
        try {
            numberOfThreads = Integer.decode(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("First parameter must be an integer specifying number of threads to use.");
            usage();
        }

        if (numberOfThreads == 0) {
            System.err.println("You specified zero threads to use, that makes no sense.");
            usage();
        } else if (numberOfThreads > Runtime.getRuntime().availableProcessors()) {
            System.err.println("You specified more threads to use than it is available.");
            System.err.println("That makes no sense, but is not critical.");
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
                System.err.println(e + " at " + i + " argument");
                softlyInterrupt(testers);
                usage();
            }
        }

        softlyInterrupt(testers);
    }

    /**
     * Wait while testers finish all jobs in the list
     * then interrupt them, but wait for last job to be finished.
     */
    protected static void softlyInterrupt(Tester[] testers) {
        try { synchronized (Thread.currentThread()) {
            while (!jobs.isEmpty()) {
                // Give testers some time to check remaining tests.
                Thread.currentThread().wait(10);
            }

            for (Tester t : testers) t.interrupt();

            boolean aliveTesters;
            do {
                aliveTesters = false;
                for (Tester t : testers) {
                    if (t.isAlive()) {
                        aliveTesters = true;
                        break;
                    }
                }
                // Give alive testers some time to finish their last test.
                Thread.currentThread().wait(10);
            } while (aliveTesters);
        }} catch (InterruptedException e) { /* Our waits won't be interrupted. */ }
        logger.logToFile();
    }

    private static void usage() {
        System.err.printf("\nUsage:\n" +
                "\tjava -cp ijunit.jar;<tested-classes> sc.ijunit.core.Main N class-name [class-name]*\n" +
                "\nYou can also specify the following options to beautify logs a bit:\n" +
                "\t-log <path to log file>    to output sorted by tester id log to file;\n" +
                "\t-noThreadIDsInLog          to omit tester id from resulting log file;\n" +
                "\t-silent                    to disable instant output to stdout.\n" +
                "These parameters may be placed anywhere in the command line after 'sc.ijunit.core.Main'.\n");
        System.exit(-1);
    }
}
