package me.johnking.fusenet.network;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.packet.core.EmitEventRequest;
import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;
import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.network.event.NetworkListener;
import me.johnking.fusenet.network.event.RequestHandler;
import me.johnking.fusenet.network.event.ResultHandler;
import me.johnking.fusenet.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marco on 18.10.2014.
 */
public class NetworkController implements NetworkManager{

    private final Proxy proxy;
    private ConnectionHandler handler;
    private final Map<String, Set<PluginNetworkListener>> listeners;
    private final Map<UUID, ResultHandler> requests;
    private final Set<PluginRequestHandler> handlers;

    public NetworkController(Proxy proxy) {
        this.proxy = proxy;
        this.handler = new ConnectionHandler(this.proxy, this);
        this.listeners = new ConcurrentHashMap<>();
        this.requests = new ConcurrentHashMap<>();
        this.handlers = Collections.newSetFromMap(new ConcurrentHashMap<PluginRequestHandler, Boolean>());
    }

    public void init() {
        this.handler.init();
    }

    @Override
    public void sendEvent(String event, NetworkEvent data) {
        EmitEventRequest request = new EmitEventRequest();
        request.setEvent(event);
        request.setData(data);
        request.setSender(this.handler.getName());

        this.handler.writeAndFlush(request);
    }

    @Override
    public void sendRequest(String target, ResultHandler resultHandler) {
        GeneralRequest request = new GeneralRequest();
        request.setTarget(target);
        request.setRequest(resultHandler.getRequest());
        request.setHandle(UUID.randomUUID());
        request.setSender(this.handler.getName());

        this.requests.put(request.getHandle(), resultHandler);
        this.handler.writeAndFlush(request);
    }

    @Override
    public void registerListener(Plugin plugin, String event, NetworkListener listener) {
        if(plugin == null || !plugin.isRegistered() || !plugin.isEnabled()) {
            return;
        }
        PluginNetworkListener networkListener = new PluginNetworkListener(plugin, listener);
        Set<PluginNetworkListener> networkListeners = this.listeners.get(event);
        if(networkListeners == null) {
            this.listeners.put(event, (networkListeners = Collections.newSetFromMap(new ConcurrentHashMap<PluginNetworkListener, Boolean>())));
        }
        for(PluginNetworkListener other : networkListeners) {
            if(other.getListener() == listener) {
                //listener is already registered for this event
                return;
            }
        }
        if(networkListeners.size() == 0) {
            this.handler.sendRegisterEvent(event);
        }
        networkListeners.add(networkListener);
    }

    @Override
    public void unregisterListener(NetworkListener listener) {
        for(Map.Entry<String, Set<PluginNetworkListener>> entry : this.listeners.entrySet()) {
            for(Iterator<PluginNetworkListener> iterator = entry.getValue().iterator(); iterator.hasNext();) {
                PluginNetworkListener networkListener = iterator.next();
                if(networkListener.getListener() == listener) {
                    iterator.remove();
                }
            }
            if(entry.getValue().size() == 0) {
                this.handler.sendUnregisterEvent(entry.getKey());
            }
        }
    }

    @Override
    public void unregisterListeners(Plugin plugin) {
        for(Map.Entry<String, Set<PluginNetworkListener>> entry : this.listeners.entrySet()) {
            for(Iterator<PluginNetworkListener> iterator = entry.getValue().iterator(); iterator.hasNext();) {
                PluginNetworkListener networkListener = iterator.next();
                if(networkListener.getPlugin() == plugin) {
                    iterator.remove();
                }
            }
            if(entry.getValue().size() == 0) {
                this.handler.sendUnregisterEvent(entry.getKey());
            }
        }
    }

    @Override
    public void addRequestHandler(Plugin plugin, RequestHandler handler) {
        if(plugin == null || !plugin.isRegistered() || !plugin.isEnabled()) {
            return;
        }
        for(PluginRequestHandler pluginHandler : this.handlers) {
            if(pluginHandler.getHandler() == handler) {
                //this handler is already registered
                return;
            }
        }
        this.handlers.add(new PluginRequestHandler(plugin, handler));
    }

    @Override
    public void removeRequestHandler(RequestHandler handler) {
        for(Iterator<PluginRequestHandler> iterator = this.handlers.iterator(); iterator.hasNext();) {
            PluginRequestHandler pluginHandler = iterator.next();
            if(pluginHandler.getHandler() == handler) {
                iterator.remove();
            }
        }
    }

    @Override
    public void removeRequestHandlers(Plugin plugin) {
        for(Iterator<PluginRequestHandler> iterator = this.handlers.iterator(); iterator.hasNext();) {
            PluginRequestHandler pluginHandler = iterator.next();
            if(pluginHandler.getPlugin() == plugin) {
                iterator.remove();
            }
        }
    }

    @Override
    public String getConnectionName() {
        return this.handler.getName();
    }

    public Map<UUID, ResultHandler> getRequests() {
        return requests;
    }

    public Set<PluginRequestHandler> getHandlers() {
        return handlers;
    }

    public Map<String, Set<PluginNetworkListener>> getListeners() {
        return listeners;
    }

    public void stop() {
        this.handler.stop();
    }

    public static final class PluginNetworkListener {

        private final Plugin plugin;
        private final NetworkListener listener;

        private PluginNetworkListener(Plugin plugin, NetworkListener listener) {
            this.plugin = plugin;
            this.listener = listener;
        }

        public Plugin getPlugin() {
            return plugin;
        }

        public NetworkListener getListener() {
            return listener;
        }
    }

    public static final class PluginRequestHandler {

        private final Plugin plugin;
        private final RequestHandler handler;

        private PluginRequestHandler(Plugin plugin, RequestHandler handler) {
            this.plugin = plugin;
            this.handler = handler;
        }

        public Plugin getPlugin() {
            return plugin;
        }

        public RequestHandler getHandler() {
            return handler;
        }
    }
}
