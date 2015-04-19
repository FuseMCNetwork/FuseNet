package me.johnking.fusenet.command.terminal;

import jline.console.ConsoleReader;
import me.johnking.fusenet.Proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 16.10.2014.
 */
public class OutputController {

    private final BlockingQueue<String> queue;
    private final OutputStream outputStream;
    private final Proxy proxy;
    private final TerminalConsoleWriterThread writerThread;

    public OutputController(Proxy proxy, OutputStream outputStream) {
        this.queue = new LinkedBlockingQueue<>(250);
        this.outputStream = outputStream;
        this.proxy = proxy;

        this.writerThread = new TerminalConsoleWriterThread();
    }

    public void addToQueue(String string) {
        if (queue.size() >= 250) {
            queue.clear();
        }
        queue.add(string);
    }

    public String getNextLog() {
        try {
            return queue.take();
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    public TerminalConsoleWriterThread getWriterThread() {
        return writerThread;
    }

    private final class TerminalConsoleWriterThread extends Thread {

        public TerminalConsoleWriterThread() {
            super("WriterThread");
            this.setDaemon(true);
            this.start();
        }

        public void run() {
            while (true) {
                String input = getNextLog();
                if (input == null) {
                    continue;
                }
                String[] messages = input.split("\n");
                for(String message : messages) {
                    try {
                        proxy.getConsoleReader().print(ConsoleReader.RESET_LINE + "");
                        proxy.getConsoleReader().flush();
                        outputStream.write((message + '\n').getBytes());
                        outputStream.flush();

                        try {
                            proxy.getConsoleReader().drawLine();
                        } catch (Throwable ex) {
                            proxy.getConsoleReader().getCursorBuffer().clear();
                        }
                        proxy.getConsoleReader().flush();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
}
