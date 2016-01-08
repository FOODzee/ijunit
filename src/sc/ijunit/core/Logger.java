package sc.ijunit.core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class used to log testers output.
 *
 * @author foodzee.
 */
class Logger {
    /**
     * Separator between tester id and message.
     */
    static final String sep = " | ";

    private boolean silent  = false;
    private PrintWriter log = null;
    private ArrayList<String> strings;
    private boolean noThreadIDsInLog = false;

    /**
     * Init logger from command line arguments and remove them
     *
     * @param a command line arguments
     * @return command line arguments without logger's
     * @throws InvalidArgumentException if log file not specified or cannot be opened
     */
    public String[] init(String[] a) throws InvalidArgumentException {
        ArrayDeque<String> args = new ArrayDeque<>(Arrays.asList(a));
        Iterator<String> iter = args.iterator();
        while (iter.hasNext()) {
            String arg = iter.next();
            if (arg.equalsIgnoreCase("-log")) {
                iter.remove();

                if (!iter.hasNext()) {
                    throw new InvalidArgumentException("-log parameter must be followed by string specifying log file.");
                }

                String logName = iter.next();
                try {
                    log = new PrintWriter(logName);
                    strings = new ArrayList<>();
                } catch (FileNotFoundException e) {
                    throw new InvalidArgumentException("File you specified as log cannot be opened/created.");
                }

                iter.remove();
            } else if (arg.equalsIgnoreCase("-silent")) {
                iter.remove();
                silent = true;
            } else if (arg.equalsIgnoreCase("-noThreadIDsInLog")) {
                iter.remove();
                noThreadIDsInLog = true;
            }
        }
        return args.toArray(new String[args.size()]);
    }

    /**
     * Saves given string to log.
     * Echoes it to standard output if only `-silent` option wasn't specified.
     *
     * @param s String to log
     */
    void log(String s) {
        if (!silent) System.out.printf(s);
        if (log != null) strings.add(s);
    }

    /**
     * Groups logged lines by tester id and writes to file.
     * If `-noThreadIDsInLog` option was specified testers' ids will be truncated.
     */
    void logToFile() {
        if (log != null) {
            strings.sort((s1, s2) -> testerId(s1).compareTo(testerId(s2)));

            for (String s : strings) {
                log.write(noThreadIDsInLog ? s.substring(s.indexOf(sep) + sep.length()) : s);
            }

            log.close();
        }
    }

    private String testerId(String s1) {
        return s1.substring(0, s1.indexOf(sep));
    }

    static class InvalidArgumentException extends Throwable {
        InvalidArgumentException(String msg) { super(msg); }
    }
}
