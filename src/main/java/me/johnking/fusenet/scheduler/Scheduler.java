package me.johnking.fusenet.scheduler;

import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.plugin.Plugin;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marco on 16.10.2014.
 */
public class Scheduler{

    private Set<SchedulerTask> tasks = Collections.newSetFromMap(new ConcurrentHashMap<SchedulerTask, Boolean>());
    private Proxy proxy;
    private int currentID;

    public Scheduler(Proxy proxy) {
        this.proxy = proxy;
    }

    final void tick(int currentTick) {
        for(Iterator<SchedulerTask> iterator = tasks.iterator(); iterator.hasNext();) {
            SchedulerTask task = iterator.next();
            if(task.tick(currentTick)) {
                iterator.remove();
            }
        }
    }

    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, int delay, ScheduleUnit unit) {
        if(plugin == null || !plugin.isRegistered() || !plugin.isEnabled()) {
            return -1;
        }
        int ticks = unit.getTicks() * delay;
        SchedulerTask task = new SchedulerTask(plugin, currentID++, true, proxy.getCurrentTick() - 1, ticks, runnable);
        this.tasks.add(task);
        return task.getID();
    }

    public int scheduleSyncDelayedTack(Plugin plugin, Runnable runnable, int delay, ScheduleUnit unit) {
        if(plugin == null || !plugin.isRegistered() || !plugin.isEnabled()) {
            return -1;
        }
        int ticks = unit.getTicks() * delay;
        SchedulerTask task = new SchedulerTask(plugin, currentID++, false, proxy.getCurrentTick() - 1, ticks, runnable);
        this.tasks.add(task);
        return task.getID();
    }

    public void cancelTask(int id) {
        for(Iterator<SchedulerTask> iterator = tasks.iterator(); iterator.hasNext();) {
            SchedulerTask task = iterator.next();
            if(task.getID() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public void cancelTasks(Plugin plugin) {
        if(plugin == null || !plugin.isRegistered()) {
            return;
        }
        for(Iterator<SchedulerTask> iterator = tasks.iterator(); iterator.hasNext();) {
            SchedulerTask task = iterator.next();
            if(task.getPlugin() == plugin) {
                iterator.remove();
            }
        }
    }
}
