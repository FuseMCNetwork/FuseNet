package me.johnking.fusenet.plugin;

/**
 * Created by Marco on 16.10.2014.
 */
public class PluginDescription {

    private String name;
    private String version;
    private int priority;
    private boolean enable;

    public PluginDescription(String name, String version, int priority, boolean enable) {
        this.name = name;
        this.version = version;
        this.priority = priority;
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getPriority() {
        return priority;
    }

    public boolean shouldEnable() {
        return enable;
    }
}
