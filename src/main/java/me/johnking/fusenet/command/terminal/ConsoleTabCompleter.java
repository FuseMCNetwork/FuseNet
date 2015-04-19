package me.johnking.fusenet.command.terminal;

import jline.console.completer.Completer;
import me.johnking.fusenet.command.CommandRegistry;

import java.util.List;

/**
 * Created by Marco on 17.10.2014.
 */
public class ConsoleTabCompleter implements Completer {

    private final CommandRegistry registry;

    public ConsoleTabCompleter(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> charSequences) {
        charSequences.addAll(registry.onTabComplete(buffer));
        final int lastSpace = buffer.lastIndexOf(' ');
        if (lastSpace == -1) {
            return cursor - buffer.length();
        } else {
            return cursor - (buffer.length() - lastSpace - 1);
        }
    }
}
