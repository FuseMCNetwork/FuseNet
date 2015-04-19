package me.johnking.fusenet.network.event;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;

/**
 * Created by Marco on 18.10.2014.
 */
public class BroadcastEvent {

    private final String name;
    private final String sender;
    private final NetworkEvent data;

    public BroadcastEvent(String name, String sender, NetworkEvent data) {
        this.name = name;
        this.sender = sender;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getSender() {
        return sender;
    }

    public NetworkEvent getData() {
        return data;
    }
}
