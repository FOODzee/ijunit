package sc.ijunit.core;

import java.util.Vector;

public class Main {

    static volatile Vector<Class> jobs;

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

        Tester[] testers = new Tester[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            testers[i] = new Tester();
            testers[i].start();
        }

        for (int i = 1; i <= numberOfJobs; i++) {
            try {
                jobs.add(Class.forName(args[i]));
            } catch (ClassNotFoundException e) {
                System.out.println(e + " at position " + i);
                usage();
            }
        }

        while (!jobs.isEmpty()) synchronized (Thread.currentThread()) {
            try   { Thread.currentThread().wait(10); }
            catch ( InterruptedException e )
            { /* Give testers some time to check remaining tests. */ }
        }

        for (Tester t : testers) t.interrupt();
    }

    private static void usage() {
        System.out.printf("Usage:\n\tjava -cp ijunit.jar;<tested-classes> sc.ijunit.core.Main N class-name [class-name]*");
        System.exit(-1);
    }
}
