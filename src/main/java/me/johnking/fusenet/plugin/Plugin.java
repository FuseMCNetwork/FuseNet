package me.johnking.fusenet.plugin;

import me.johnking.fusenet.Proxy;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 15.10.2014.
 */
public abstract class Plugin {

    private Proxy proxy;
    private Logger logger;
    private PluginDescription description;
    private boolean enabled;
    private boolean registered;

    public abstract void onEnable();

    public abstract void onDisable();

    public File getDataFolder() {
        return new File(getProxy().getPluginManager().getDirectory(), this.description.getName());
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginDescription getDescription() {
        return description;
    }

    final void setEnabled(boolean enabled) {
        if(this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if(this.enabled) {
            try {
                onEnable();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error while enabling Plugin", ex);
            }
        } else {
            try {
                onDisable();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error while disabling Plugin", ex);
            }
        }
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final boolean isRegistered() {
        return registered;
    }

    final void init(Proxy proxy) {
        PluginData data = getClass().getAnnotation(PluginData.class);
        this.description = new PluginDescription(data.name(), data.version(), data.priority(), data.enable());
        this.proxy = proxy;
        this.logger = new PluginLogger(this);

        this.registered = true;
        this.logger.log(Level.INFO, "Loading plugin version " + this.description.getVersion() + "!");
    }
}
