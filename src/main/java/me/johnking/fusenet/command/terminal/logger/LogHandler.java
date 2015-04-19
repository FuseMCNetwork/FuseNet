package me.johnking.fusenet.command.terminal.logger;

import me.johnking.fusenet.Proxy;

import java.util.logging.*;

/**
 * Created by Marco on 17.10.2014.
 */
public class LogHandler extends Handler {

    private final Proxy proxy;

    public LogHandler(Proxy proxy) {
        this.proxy = proxy;
        setLevel(Level.INFO);
        setFormatter(new LogFormatter());
    }

    @Override
    public void publish(LogRecord logRecord) {
        try {
            this.proxy.getOutputController().addToQueue(getFormatter().format(logRecord));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}
}
