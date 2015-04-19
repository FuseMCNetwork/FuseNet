package me.johnking.fusenet;

import jline.console.ConsoleReader;
import me.johnking.fusenet.command.CommandRegistry;
import me.johnking.fusenet.command.terminal.OutputController;
import me.johnking.fusenet.command.terminal.logger.LogHandler;
import me.johnking.fusenet.command.terminal.logger.LogOutputStream;
import me.johnking.fusenet.network.NetworkController;
import me.johnking.fusenet.plugin.PluginCoreController;
import me.johnking.fusenet.scheduler.ProxyThread;
import me.johnking.fusenet.scheduler.ScheduleUnit;
import me.johnking.fusenet.scheduler.Scheduler;
import me.johnking.fusenet.util.ProxyShutdownHook;
import me.johnking.fusenet.util.SleepingThread;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by Marco on 15.10.2014.
 */
public class Proxy {

    private final Logger logger = Logger.getLogger("FuseNet");
    private ConsoleReader reader;
    private final OutputController outputController;
    private final CommandRegistry commandRegistry;
    private final NetworkController networkController;

    private final Scheduler scheduler;
    private final PluginCoreController pluginController;

    private final ProxyThread primaryThread;

    private final ProxyShutdownHook shutdownHook;
    private boolean running;

    Proxy () {
        ///////////////////initialize the command-interface///////////////////
        try {
            this.reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e){
            System.out.println("Could not load console reader!");
            System.exit(2);
        }
        this.running = true;
        this.outputController = new OutputController(this, System.out);

        Logger global = Logger.getLogger("");
        global.setUseParentHandlers(false);
        for(Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new LogHandler(this));

        System.setOut(new PrintStream(new LogOutputStream(this.logger), true));
        System.setErr(new PrintStream(new LogOutputStream(this.logger), true));
        this.commandRegistry = new CommandRegistry(this);
        System.out.println("CommandRegistry loaded!");
        /////////////////////////enabling connection//////////////////////////
        this.networkController = new NetworkController(this);
        this.networkController.init();
        ///////////////////////////initialize core////////////////////////////
        FuseNet.init(this);
        System.out.println("Enabling Scheduler!");
        this.scheduler = new Scheduler(this);
        this.pluginController = new PluginCoreController(this);

        this.primaryThread = new ProxyThread(this);
        this.commandRegistry.init();
        System.out.println("Loading Plugins!");
        this.pluginController.init();

        this.primaryThread.start();
        //////////////////////////////////////////////////////////////////////
        this.shutdownHook = new ProxyShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        new SleepingThread();
        System.out.println("Done!");
    }

    public void shutdown() {
        if(this.shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
        }
        this.reader.setPrompt("");
        disable();
        try {
            this.reader.getTerminal().restore();
        } catch (Exception e) {
        } finally {
            System.exit(0);
        }
    }

    public void disable() {
        //////////////////////////////////////////////////////////////////////
        if(this.pluginController != null) {
            System.out.println("Disabling Plugins!");
            this.pluginController.stop();
        }
        if(this.networkController != null) {
            this.networkController.stop();
        }
        //////////////////////////////////////////////////////////////////////
    }

    public void stop() {
        this.running = false;
    }

    public Logger getLogger() {
        return logger;
    }

    public ConsoleReader getConsoleReader() {
        return reader;
    }

    public OutputController getOutputController() {
        return outputController;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public PluginCoreController getPluginManager() {
        return this.pluginController;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public NetworkController getNetworkController() {
        return networkController;
    }

    public int getCurrentTick() {
        return this.primaryThread.getCurrentTick();
    }

    public boolean isRunning() {
        return this.running;
    }
}
