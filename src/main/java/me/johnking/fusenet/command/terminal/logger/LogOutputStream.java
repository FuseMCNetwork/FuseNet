package me.johnking.fusenet.command.terminal.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 16.10.2014.
 */
public class LogOutputStream extends ByteArrayOutputStream {

    private static final String SEPARATOR = System.getProperty("line.separator");
    private final Logger logger;

    public LogOutputStream(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String record = this.toString();
            super.reset();
            if ((record.length() > 0) && (!record.equals(SEPARATOR))) {
                logger.log(Level.INFO, record);
            }
        }
    }
}
