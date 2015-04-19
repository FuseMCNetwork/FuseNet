package me.johnking.fusenet.command.terminal;

import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.command.CommandRegistry;

import java.io.IOException;

/**
 * Created by Marco on 17.10.2014.
 */
public class CommandLineReader extends Thread {

    private final Proxy proxy;

    public CommandLineReader(Proxy proxy, CommandRegistry registry) {
        super("CommandReader");
        this.proxy = proxy;
        try {
            this.proxy.getConsoleReader().setPrompt(">");

            this.proxy.getConsoleReader().addCompleter(new ConsoleTabCompleter(registry));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        setDaemon(true);
        start();
    }

    @Override
    public void run(){
        String line;
        try {
            while (this.proxy.isRunning()) {
                line = this.proxy.getConsoleReader().readLine();
                if(line == null) {
                    break;
                }
                try {
                    if(line.equals("")){
                        continue;
                    }
                    this.proxy.getCommandRegistry().onCommand(line);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
