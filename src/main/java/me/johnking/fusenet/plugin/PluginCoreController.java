package me.johnking.fusenet.plugin;

import me.johnking.fusenet.Proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Marco on 15.10.2014.
 */
public class PluginCoreController implements PluginManager{

    private Proxy proxy;
    private File directory;
    private File[] jars;
    private PluginLoader pluginLoader;
    private Plugin[] plugins;

    public PluginCoreController(Proxy proxy) {
        this.proxy = proxy;
        this.directory = new File("plugins");
        if(!this.directory.exists()) {
            this.directory.mkdir();
        }
        this.jars = directory.listFiles(new JarFileFilter());
        this.pluginLoader = new PluginLoader(jars);
    }

    public void init() {
        this.plugins = sortPlugins(loadPlugins(), PluginOrder.STARTUP);

        for(Plugin plugin : plugins) {
            if(plugin.getDescription().shouldEnable()) {
                plugin.getLogger().log(Level.INFO, "Enabling!");
                plugin.setEnabled(true);
            }
        }
    }

    public void stop() {
        this.plugins = sortPlugins(this.plugins, PluginOrder.STOP);

        for(Plugin plugin : plugins) {
            if(plugin.isEnabled()) {
                plugin.getLogger().log(Level.INFO, "Disabling!");
                plugin.setEnabled(false);
            }
        }
    }

    public Plugin[] loadPlugins() {
        ArrayList<Plugin> loaded = new ArrayList<>();
        for(File plugin : jars) {
            Class<?> main = pluginLoader.loadPlugin(plugin);
            if(main == null) {
                this.proxy.getLogger().log(Level.SEVERE, "Could not load " + plugin.getName() + ":", new InvalidPluginException("There to many/few main class(es)! Needed: 1!"));
                continue;
            }
            try {
                Object instance = main.newInstance();
                if(instance instanceof Plugin) {
                    loaded.add((Plugin) instance);
                } else {
                    new InvalidPluginException("Could not load "+ plugin.getName() + ": Please contact the developer!").printStackTrace();
                }
            } catch (InstantiationException | IllegalAccessException e) {
                new InvalidPluginException("Could not load "+ plugin.getName() + ": Could not instantiate main class!").printStackTrace();
            }
        }
        for(Plugin plugin : loaded) {
            plugin.init(proxy);
        }
        return loaded.toArray(new Plugin[loaded.size()]);
    }

    private Plugin[] sortPlugins(Plugin[] plugins, PluginOrder order) {
        Plugin tmp;
        for(int i = 0; i < plugins.length; i++) {
            int j = i;
            for(int k = i; k < plugins.length; k++) {
                if(order.sort(plugins[j], plugins[k])){
                    j = k;
                }
            }
            tmp = plugins[i];
            plugins[i] = plugins[j];
            plugins[j] = tmp;
        }
        return plugins;
    }

    @Override
    public File getDirectory() {
        return directory;
    }

    @Override
    public Plugin getPlugin(String name) {
        for(Plugin plugin : plugins) {
            if(plugin.getDescription().getName().equals(name)) {
                return plugin;
            }
        }
        return null;
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if(plugin == null || !plugin.isRegistered()) {
            return;
        }
        if(plugin.isEnabled()) {
            return;
        }
        this.proxy.getLogger().log(Level.INFO, "Enabling " + plugin.getDescription().getName() + "!");
        plugin.setEnabled(true);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if(plugin == null || !plugin.isRegistered()) {
            return;
        }
        if(!plugin.isEnabled()) {
            return;
        }
        this.proxy.getLogger().log(Level.INFO, "Disabling " + plugin.getDescription().getName() + "!");
        plugin.setEnabled(false);
        this.proxy.getCommandRegistry().unregisterCommands(plugin);
        this.proxy.getScheduler().cancelTasks(plugin);
        this.proxy.getNetworkController().removeRequestHandlers(plugin);
        this.proxy.getNetworkController().unregisterListeners(plugin);
    }

    public void reload() {
        //TODO : reload command
    }

    public static enum PluginOrder {

        STARTUP() {
            @Override
            public boolean sort(Plugin plugin, Plugin other) {
                return plugin.getDescription().getPriority() > other.getDescription().getPriority();
            }
        },
        STOP() {
            @Override
            public boolean sort(Plugin plugin, Plugin other) {
                return plugin.getDescription().getPriority() < other.getDescription().getPriority();
            }
        };

        PluginOrder() {

        }

        public abstract boolean sort(Plugin plugin, Plugin other);
    }
}
