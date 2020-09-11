package com.arya1021.alipay.request;

import org.java_websocket.client.WebSocketClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 收款号websocket单例。防止建立重复连接 造成资源浪费。
 */
public final class WsocketChannelAccountSingleton {

    private WsocketChannelAccountSingleton(){}

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     * key = 商户uid+手机序列号
     */
    private ConcurrentHashMap<String, WebSocketClient> webSocketMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, WebSocketClient> getWebSocketMap() {
        return webSocketMap;
    }

    static class InnerClz{

        private static final WsocketChannelAccountSingleton instance = new WsocketChannelAccountSingleton();

    }

    public static WsocketChannelAccountSingleton getInstance(){
        return InnerClz.instance;
    }
}
