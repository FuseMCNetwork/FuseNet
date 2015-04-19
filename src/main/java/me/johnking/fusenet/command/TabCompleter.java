package me.johnking.fusenet.command;

import java.util.List;

/**
 * Created by Marco on 15.10.2014.
 */
public abstract interface TabCompleter {

    public abstract List<String> onTabComplete(String[] args);
}
