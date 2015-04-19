package me.johnking.fusenet;

import me.johnking.fusenet.command.CommandManager;
import me.johnking.fusenet.network.NetworkManager;
import me.johnking.fusenet.plugin.PluginManager;
import me.johnking.fusenet.scheduler.Scheduler;

import java.util.logging.Logger;

/**
 * Created by Marco on 15.10.2014.
 */
public class FuseNet {

    private static Proxy proxy;

    static void init(Proxy proxy) {
        FuseNet.proxy = proxy;
    }

    public static Logger getLogger() {
        return proxy.getLogger();
    }

    public static CommandManager getCommandManager() {
        return proxy.getCommandRegistry();
    }

    public static PluginManager getPluginManager() {
        return proxy.getPluginManager();
    }

    public static Scheduler getScheduler() {
        return proxy.getScheduler();
    }

    public static NetworkManager getNetworkManager() {
        return proxy.getNetworkController();
    }
}
