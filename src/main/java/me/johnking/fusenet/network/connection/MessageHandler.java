package me.johnking.fusenet.network.connection;

import com.xxmicloxx.znetworklib.InboundHandler;
import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.packet.core.*;
import io.netty.channel.ChannelHandlerContext;
import me.johnking.fusenet.network.ConnectionHandler;

import java.util.logging.Level;

/**
 * Created by Marco on 18.10.2014.
 */
public class MessageHandler extends InboundHandler {

    private final ConnectionHandler controller;
    private ChannelHandlerContext ctx;

    public MessageHandler(ConnectionHandler controller) {
        this.controller = controller;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.controller.getLogger().log(Level.INFO, "Requesting name!");
        this.ctx = ctx;
        HaveNameRequest request = new HaveNameRequest();
        request.setDesiredName(System.getProperty("network.name"));
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.controller.channelInactive();
        if(this.controller.shouldShutdown()) {
            this.controller.getLogger().log(Level.INFO, "Shutting down connection...");
        } else {
            this.controller.getLogger().log(Level.SEVERE, "Connection closed! Reconnecting...");
            ConnectorThread thread = new ConnectorThread(this.controller);
            thread.start();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(this.controller.getNameUnsafe() == null && !(msg instanceof HaveNameResult)) {
            return;
        }
        if(msg instanceof HaveNameResult) {
            handleNameResult(ctx, (HaveNameResult) msg);
        } else if(msg instanceof GeneralRequest) {
            this.controller.handleRequest((GeneralRequest) msg, ctx);
        } else if(msg instanceof GeneralRequestTargetNotFound) {
            this.controller.handleResultError((GeneralRequestTargetNotFound) msg);
        } else if(msg instanceof GeneralResult) {
            this.controller.handleResult((GeneralResult) msg);
        } else if(msg instanceof EventEmittedRequest) {
            this.controller.handleEvent((EventEmittedRequest) msg);
        }
    }

    private void handleNameResult(ChannelHandlerContext ctx, HaveNameResult result) {
        if(!result.isSuccessful()) {
            this.controller.getLogger().log(Level.SEVERE, "Network name already taken!");
            this.controller.stopProxy();
            return;
        }
        String name = System.getProperty("network.name");
        this.controller.connectionEstablished(name, this, ctx);

        this.controller.getLogger().log(Level.INFO, "Connection established!");
    }

    public void writeAndFlush(NetworkPacket request) {
        this.ctx.writeAndFlush(request);
    }
}
