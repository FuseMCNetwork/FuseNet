package me.johnking.fusenet.command.terminal.logger;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by Marco on 17.08.2014.
 */
public class LogFormatter extends Formatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord logRecord) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append(DATE_FORMAT.format(logRecord.getMillis()));
        buffer.append(" ").append(logRecord.getLevel()).append("]: ");
        buffer.append(logRecord.getMessage()).append('\n');
        return buffer.toString();
    }
}
