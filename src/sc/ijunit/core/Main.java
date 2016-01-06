package sc.ijunit.core;

import java.util.Vector;

public class Main {

    static volatile Vector<Class> jobs;

    public static void main(String[] args) {
        final int numberOfJobs = args.length - 1;
        if (numberOfJobs < 1) {
            usage();
        }

        int numberOfThreads = 0;
        try {
            numberOfThreads = Integer.decode(args[0]);
        } catch (NumberFormatException e) {
            usage();
        }
        Tester[] testers = new Tester[numberOfThreads];
        for (Tester t : testers) t.run();

        jobs = new Vector<>(numberOfJobs);
        for (int i = 1; i <= numberOfJobs; i++) {
            try {
                jobs.add(Class.forName(args[i]));
            } catch (ClassNotFoundException e) {
                System.out.println(e + " at position " + i);
                usage();
            }
        }

        for (Tester t : testers) t.notify();
    }

    private static void usage() {
        System.out.println("java -cp ijunit.jar;<tested-classes> sc.ijunit.core.Main N class-name [class-name]*");
        System.exit(-1);
    }
}
