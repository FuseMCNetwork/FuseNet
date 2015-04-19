package me.johnking.fusenet.network.event;

import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;

/**
 * Created by Marco on 18.10.2014.
 */
public abstract interface ResultHandler {

    public abstract Request getRequest();

    public abstract void onResult(Result result);

    public abstract void onRequestFail();
}
