package me.johnking.fusenet.plugin;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Marco on 15.10.2014.
 */
public class JarFileFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(".jar");
    }
}
