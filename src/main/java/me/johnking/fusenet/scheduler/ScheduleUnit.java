package me.johnking.fusenet.scheduler;

/**
 * Created by Marco on 16.10.2014.
 */
public enum ScheduleUnit {

    TICKS(1),
    SECONDS(20),
    MINUTES(1200),
    HOURS(72000);

    private int ticks;

    ScheduleUnit(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
