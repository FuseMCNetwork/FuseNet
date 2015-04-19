package me.johnking.fusenet.command;

import me.johnking.fusenet.plugin.Plugin;

/**
 * Created by Marco on 17.10.2014.
 */
public abstract interface CommandManager {

    public abstract void registerCommand(Plugin plugin, Command command);

    public abstract void unregisterCommand(Command command);

    public abstract void unregisterCommands(Plugin plugin);
}
