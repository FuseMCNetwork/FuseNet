package me.johnking.fusenet.util;

/**
 * Created by Marco on 17.10.2014.
 */
public class SleepingThread extends Thread {

    public SleepingThread() {
        super("SleepingThread");

        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                Thread.sleep(2147483647L);
            }
        } catch (InterruptedException e) {

        }
    }
}
