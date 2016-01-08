package sc.ijunit.core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class used to log testers output.
 *
 * @author foodzee.
 */
final class Logger {

    private boolean silent = false;
    private boolean noThreadIDsInLog = false;
    private PrintWriter log = null;
    private ArrayList<Message> messages;

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
                    messages = new ArrayList<>();
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
     * Saves given message to log.
     * Echoes it to standard output if only `-silent` option wasn't specified.
     */
    void log(Message msg) {
        if (!silent) System.out.printf(msg.full());
        if (log != null) messages.add(msg);
    }

    /**
     * Groups logged lines by tester id and writes to file.
     * If `-noThreadIDsInLog` option was specified testers' ids will be truncated.
     */
    void logToFile() {
        if (log != null) {
            messages.sort(null); // Sort methods using natural ordering defined by `compareTo`

            for (Message msg : messages) {
                log.write(noThreadIDsInLog ? msg.getMsg() : msg.full());
            }

            log.close();
        }
    }

    static class InvalidArgumentException extends Throwable {
        InvalidArgumentException(String msg) { super(msg); }
    }

    static class Message implements Comparable<Message> {
        private final long testerID;
        private final String msg;

        Message(long testerID, String msg) {
            this.testerID = testerID;
            this.msg = msg;
        }

        String getMsg() {
            return msg;
        }

        String full() {
            return testerID + " | " + msg;
        }

        @Override
        public int compareTo(Message that) {
            if (that == null) return 1;
            if (this.testerID > that.testerID)  return 1;
            if (this.testerID == that.testerID) return 0;
            return -1;
        }
    }
}
