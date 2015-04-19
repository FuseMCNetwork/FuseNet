package me.johnking.fusenet.plugin;

import java.io.File;

/**
 * Created by Marco on 15.10.2014.
 */
public abstract interface PluginManager {

    public abstract File getDirectory();

    public abstract Plugin getPlugin(String name);

    public abstract void enablePlugin(Plugin plugin);

    public abstract void disablePlugin(Plugin plugin);
}
