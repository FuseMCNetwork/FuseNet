package me.johnking.fusenet.util;

import me.johnking.fusenet.Proxy;

/**
 * Created by Marco on 17.10.2014.
 */
public class ProxyShutdownHook extends Thread {

    private Proxy proxy;

    public ProxyShutdownHook(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void run() {
        this.proxy.getConsoleReader().setPrompt("");
        this.proxy.disable();
        try {
            this.proxy.getConsoleReader().getTerminal().restore();
        } catch (Exception exc) {

        }
    }
}
