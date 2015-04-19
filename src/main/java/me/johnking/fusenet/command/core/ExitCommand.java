package me.johnking.fusenet.command.core;

import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.command.Command;

/**
 * Created by Marco on 18.10.2014.
 */
public class ExitCommand extends Command {

    private final Proxy proxy;

    public ExitCommand(Proxy proxy) {
        super("exit");
        this.proxy = proxy;
    }

    @Override
    public boolean onCommand(String[] args) {
        proxy.stop();
        return true;
    }
}
