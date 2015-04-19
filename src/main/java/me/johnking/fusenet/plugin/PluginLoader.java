package me.johnking.fusenet.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by Marco on 15.10.2014.
 */
public class PluginLoader {

    private ClassLoader classLoader;

    public PluginLoader(File[] plugins) {
        try {
            URL[] urls = filesToURL(plugins);
            this.classLoader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private URL[] filesToURL(File[] files) throws MalformedURLException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    public Class<?> loadPlugin(File plugin) {
        Class<?> result = null;
        JarEntry entry;
        try {
            JarInputStream inputStream = new JarInputStream(new FileInputStream(plugin));
            while ((entry = inputStream.getNextJarEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".class")) {
                    try {
                        Class<?> clazz = classLoader.loadClass(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'));
                        if(clazz.getAnnotation(PluginData.class) != null && clazz.getSuperclass() == Plugin.class) {
                            if(result == null) {
                                result = clazz;
                            } else {
                                result = null;
                                break;
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Can't load Class " + entry.getName());
                        e.printStackTrace();
                    }
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
