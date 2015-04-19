package me.johnking.fusenet.command;

import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.command.core.ClearCommand;
import me.johnking.fusenet.command.core.ExitCommand;
import me.johnking.fusenet.command.terminal.CommandLineReader;
import me.johnking.fusenet.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marco on 15.10.2014.
 */
public class CommandRegistry implements CommandManager {

    private final Proxy proxy;
    private final CommandLineReader lineReader;
    private final Map<Plugin, ArrayList<Command>> commands;
    private final ArrayList<Command> systemCommands;

    public CommandRegistry (Proxy proxy) {
        this.proxy = proxy;
        this.lineReader = new CommandLineReader(this.proxy, this);
        this.commands = new ConcurrentHashMap<>();
        this.systemCommands = new ArrayList<>();
    }

    public void init() {
        addSystemCommand(new ExitCommand(this.proxy));
        addSystemCommand(new ClearCommand(this.proxy.getConsoleReader()));
    }

    public List<String> onTabComplete(String buffer) {
        List<String> results = new ArrayList<>();
        String[] cmd = buffer.split(" ");
        String[] result = new String[buffer.endsWith(" ") ? cmd.length + 1 : cmd.length];
        for(int i = 0; i < cmd.length; i++) {
            result[i] = cmd[i];
        }
        if(result.length > cmd.length) {
            result[result.length - 1] = "";
        }
        if(result.length == 0 || result.length == 1) {
            String begin = result.length == 0 ? "" : result[0].toLowerCase();
            for(Map.Entry<Plugin, ArrayList<Command>> entry : commands.entrySet()) {
                if(entry.getValue() == null) {
                    continue;
                }
                for(Command command : entry.getValue()) {
                    if(command.getName().toLowerCase().startsWith(begin)) {
                        results.add(command.getName());
                    }
                }
            }
            for(Command command : systemCommands) {
                if(command.getName().toLowerCase().startsWith(begin)) {
                    results.add(command.getName());
                }
            }
            return results;
        }
        Command command = getCommand(result[0]);
        if(command == null || !(command instanceof TabCompleter)) {
            return results;
        }
        String[] args = new String[result.length - 1];
        for(int i = 0; i < args.length; i++) {
            args[i] = result[i + 1];
        }
        TabCompleter completer = (TabCompleter) command;
        try {
            results = completer.onTabComplete(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public void onCommand(String buffer) {
        String[] cmd = buffer.split(" ");
        if(cmd.length == 0) {
            return;
        }
        Command command = getCommand(cmd[0]);
        if(command == null) {
            System.out.println("Unknown command!");
            return;
        }
        String[] args = new String[cmd.length - 1];
        for(int i = 0; i < args.length; i++) {
            args[i] = cmd[i + 1];
        }
        boolean success = true;
        try {
            success = command.onCommand(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!success) {
            System.out.println("Please check the format of the command(" + cmd[0] + ")!");
        }
    }

    public void addSystemCommand(Command command) {
        systemCommands.add(command);
    }

    @Override
    public void registerCommand(Plugin plugin, Command command) {
        if(plugin == null || !plugin.isRegistered() || !plugin.isEnabled()) {
            return;
        }
        if(getCommand(command.getName()) != null) {
            System.out.println("Command " + command.getName() + " is already registered!");
            return;
        }
        ArrayList<Command> commands = this.commands.get(plugin);
        if(commands == null) {
            this.commands.put(plugin, (commands = new ArrayList<>()));
        }
        commands.add(command);
    }

    @Override
    public void unregisterCommand(Command command) {
        for(Map.Entry<Plugin, ArrayList<Command>> entry : commands.entrySet()) {
            if(entry.getValue() == null) {
                continue;
            }
            if(entry.getValue().contains(command)) {
                entry.getValue().remove(command);
                break;
            }
        }
    }

    @Override
    public void unregisterCommands(Plugin plugin) {
        if(plugin == null || !plugin.isRegistered()) {
            return;
        }
        commands.remove(plugin);
    }

    public Command getCommand(String name) {
        for(Map.Entry<Plugin, ArrayList<Command>> entry : commands.entrySet()) {
            if(entry.getValue() == null) {
                continue;
            }
            for(Command command : entry.getValue()) {
                if(command.getName().equalsIgnoreCase(name)) {
                    return command;
                }
            }
        }
        for(Command command : systemCommands) {
            if(command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public CommandLineReader getLineReader() {
        return lineReader;
    }
}
