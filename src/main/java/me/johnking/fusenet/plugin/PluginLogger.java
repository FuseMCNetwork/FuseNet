package me.johnking.fusenet.plugin;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Marco on 15.10.2014.
 */
public class PluginLogger extends Logger{

    private String pluginName;

    public PluginLogger(Plugin plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        this.pluginName = "[" + plugin.getDescription().getName() + "] ";
        this.setParent(plugin.getProxy().getLogger());
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(this.pluginName + record.getMessage());
        super.log(record);
    }
}
