package me.johnking.fusenet.scheduler;

import me.johnking.fusenet.plugin.Plugin;

/**
 * Created by Marco on 16.10.2014.
 */
public class SchedulerTask{

    private Plugin plugin;
    private int id;
    private boolean repeating;
    private int tick;
    private int delay;
    private Runnable runnable;

    public SchedulerTask(Plugin plugin, int id, boolean repeating, int currentTick, int delay, Runnable runnable) {
        this.plugin = plugin;
        this.id = id;
        this.repeating = repeating;
        this.tick = currentTick;
        this.delay = delay;
        this.runnable = runnable;
    }

    public boolean tick(int currentTick) {
        if((currentTick - tick) % delay == 0) {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return !repeating;
        }
        return false;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public int getID() {
        return id;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
