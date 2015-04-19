package me.johnking.fusenet.network;

import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.codec.Result;
import com.xxmicloxx.znetworklib.packet.core.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import me.johnking.fusenet.Proxy;
import me.johnking.fusenet.network.connection.ConnectorThread;
import me.johnking.fusenet.network.connection.MessageHandler;
import me.johnking.fusenet.network.event.BroadcastEvent;
import me.johnking.fusenet.network.event.NetworkListener;
import me.johnking.fusenet.network.event.RequestHandler;
import me.johnking.fusenet.network.event.ResultHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco on 18.10.2014.
 */
public class ConnectionHandler {

    private final Proxy proxy;
    private final NetworkController controller;
    private final Logger logger = Logger.getLogger("Network");
    private final NioEventLoopGroup group;
    private MessageHandler handler;
    private String name;
    private boolean shutdown;

    private Object lock = new Object();

    public ConnectionHandler(Proxy proxy, NetworkController controller) {
        this.proxy = proxy;
        this.controller = controller;
        this.group = new NioEventLoopGroup();
    }

    public void init() {
        ConnectorThread connector = new ConnectorThread(this);
        connector.start();
        checkLock();
    }

    public void checkLock() {
        if (handler != null) {
            return;
        }
        while (handler == null) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean shouldShutdown() {
        return shutdown;
    }

    public void channelInactive() {
        name = null;
        handler = null;
    }

    public void connectionEstablished(String name, MessageHandler handler, ChannelHandlerContext ctx) {
        this.name = name;
        this.handler = handler;

        for(Map.Entry<String, Set<NetworkController.PluginNetworkListener>> entry : this.controller.getListeners().entrySet()) {
            if(entry.getValue().size() == 0) {
                continue;
            }
            RegisterListenerRequest request = new RegisterListenerRequest();
            request.setSender(this.name);
            request.setEvent(entry.getKey());
            ctx.write(request);
        }
        ctx.flush();

        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public NioEventLoopGroup getGroup() {
        return group;
    }

    public String getName() {
        checkLock();
        return name;
    }

    public String getNameUnsafe() {
        return name;
    }

    public void stopProxy() {
        proxy.shutdown();
    }

    public void stop() {
        shutdown = true;
        this.group.shutdownGracefully().syncUninterruptibly();
    }

    public void handleResultError(GeneralRequestTargetNotFound result) {
        ResultHandler handler = this.controller.getRequests().remove(result.getHandle());
        if (handler == null) {
            this.logger.log(Level.WARNING, "Received a result without having a request!");
            return;
        }
        try {
            handler.onRequestFail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleResult(GeneralResult result) {
        ResultHandler handler = this.controller.getRequests().remove(result.getHandle());
        if(handler == null) {
            this.logger.log(Level.WARNING, "Received a result without having a request!");
            return;
        }
        try {
            handler.onResult(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRequest(GeneralRequest request, ChannelHandlerContext ctx) {
        Result result = null;
        for(NetworkController.PluginRequestHandler handler : this.controller.getHandlers()) {
            try {
                result = handler.getHandler().handle(request.getSender(), request.getRequest());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(result != null) {
                break;
            }
        }
        GeneralResult generalResult = new GeneralResult();
        generalResult.setHandle(request.getHandle());
        generalResult.setResult(result);
        generalResult.setTarget(request.getSender());
        generalResult.setSender(this.getName());
        ctx.writeAndFlush(generalResult);
    }

    public void handleEvent(EventEmittedRequest msg) {
        Set<NetworkController.PluginNetworkListener> listeners = this.controller.getListeners().get(msg.getEvent());
        if(listeners == null || listeners.size() == 0) {
            this.logger.log(Level.WARNING, "Received a event without having any listener!");
            return;
        }
        BroadcastEvent event = new BroadcastEvent(msg.getEvent(), msg.getSender(), msg.getData());
        for(NetworkController.PluginNetworkListener listener : listeners) {
            try {
                listener.getListener().onEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRegisterEvent(String event) {
        RegisterListenerRequest request = new RegisterListenerRequest();
        request.setEvent(event);
        checkLock();
        request.setSender(this.name);
        this.handler.writeAndFlush(request);
    }

    public void sendUnregisterEvent(String event) {
        UnregisterListenerRequest request = new UnregisterListenerRequest();
        request.setEvent(event);
        checkLock();
        request.setSender(this.name);
        this.handler.writeAndFlush(request);
    }

    public void writeAndFlush(NetworkPacket object) {
        checkLock();
        this.handler.writeAndFlush(object);
    }
}
