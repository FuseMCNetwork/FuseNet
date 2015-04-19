package me.johnking.fusenet.network.event;

import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;

/**
 * Created by Marco on 18.10.2014.
 */
public abstract interface RequestHandler {

    public abstract Result handle(String sender, Request packet);
}
