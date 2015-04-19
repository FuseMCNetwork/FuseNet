package me.johnking.fusenet.network.event;

/**
 * Created by Marco on 18.10.2014.
 */
public abstract interface NetworkListener {

    public abstract void onEvent(BroadcastEvent event);
}
