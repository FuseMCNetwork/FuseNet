package me.johnking.fusenet.command;

/**
 * Created by Marco on 15.10.2014.
 */
public abstract class Command {

    private String name;

    public Command(String name) {
        this.name = name;
    }

    public abstract boolean onCommand(String[] args);

    public String getName() {
        return name;
    }
}
