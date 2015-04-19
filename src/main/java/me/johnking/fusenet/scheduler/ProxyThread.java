package me.johnking.fusenet.scheduler;

import me.johnking.fusenet.Proxy;

import java.util.logging.Level;

/**
 * Created by Marco on 16.10.2014.
 */
public class ProxyThread extends Thread {

    private Proxy proxy;
    private int currentTick;

    public ProxyThread(Proxy proxy) {
        super("ProxyThread");
        this.proxy = proxy;
    }

    @Override
    public void run() {
        try {
            long lastTime = System.nanoTime();
            long catchupTime = 0L;
            while(this.proxy.isRunning()) {
                long currentTime = System.nanoTime();
                long wait = 50000000L - (currentTime - lastTime) - catchupTime;
                if (wait > 0L) {
                    Thread.sleep(wait / 1000000L);
                    catchupTime = 0L;
                }
                else {
                    catchupTime = Math.min(1000000000L, Math.abs(wait));
                    lastTime = currentTime;
                    this.currentTick++;

                    this.proxy.getScheduler().tick(this.currentTick);
                }
            }
        } catch (Throwable throwable) {
            this.proxy.getLogger().log(Level.SEVERE, "Encountered an unexpected exception", throwable);
        } finally {
            this.proxy.shutdown();
        }
    }

    public int getCurrentTick() {
        return currentTick;
    }
}
