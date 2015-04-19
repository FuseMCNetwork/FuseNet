package me.johnking.fusenet.command.core;

import jline.console.ConsoleReader;
import me.johnking.fusenet.command.Command;

import java.io.IOException;

/**
 * Created by Marco on 19.10.2014.
 */
public class ClearCommand extends Command{

    private ConsoleReader reader;

    public ClearCommand(ConsoleReader reader) {
        super("clear");
        this.reader = reader;
    }

    @Override
    public boolean onCommand(String[] args) {
        try {
            reader.clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
