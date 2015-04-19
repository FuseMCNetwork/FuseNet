package me.johnking.fusenet.network;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import me.johnking.fusenet.network.event.NetworkListener;
import me.johnking.fusenet.network.event.RequestHandler;
import me.johnking.fusenet.network.event.ResultHandler;
import me.johnking.fusenet.plugin.Plugin;

/**
 * Created by Marco on 18.10.2014.
 */
public abstract interface NetworkManager {

    public abstract void sendEvent(String event, NetworkEvent data);

    public abstract void sendRequest(String target, ResultHandler resultHandler);

    public abstract void registerListener(Plugin plugin, String event, NetworkListener listener);

    public abstract void unregisterListener(NetworkListener listener);

    public abstract void unregisterListeners(Plugin plugin);

    public abstract void addRequestHandler(Plugin plugin, RequestHandler handler);

    public abstract void removeRequestHandler(RequestHandler handler);

    public abstract void removeRequestHandlers(Plugin plugin);

    public abstract String getConnectionName();
}
