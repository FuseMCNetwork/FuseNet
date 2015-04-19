package me.johnking.fusenet.network.connection;

import com.xxmicloxx.znetworklib.PipelineInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.johnking.fusenet.network.ConnectionHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Marco on 18.10.2014.
 */
public class ConnectorThread extends Thread {

    private final ConnectionHandler controller;
    private int retries = 0;

    public ConnectorThread(ConnectionHandler controller) {
        super("Connector");
        this.controller = controller;
        setDaemon(true);
    }

    private ChannelFuture connect(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(controller.getGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                PipelineInitializer.initChannel(socketChannel, false);
                socketChannel.pipeline().addLast(new MessageHandler(controller));
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap.connect("178.63.117.132", System.getProperty("destinationPort") == null ? 50000 : Integer.valueOf(System.getProperty("destinationPort")));
    }

    @Override
    public void run() {
        ChannelFuture future = connect();
        try {
            while (!future.await(5, TimeUnit.SECONDS) || future.cause() != null) {
                this.controller.getLogger().log(Level.SEVERE, "Error connecting to network server! Current retire: " + retries + "!");
                future.cancel(true);
                future.channel().close().sync();
                Thread.sleep(2000);
                /*
                if(retries == 0) {
                    this.controller.getLogger().log(Level.SEVERE, "Error connecting to network server! Shutting down!");
                    controller.stopProxy();
                }
                */
                retries++;
                future = connect();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
