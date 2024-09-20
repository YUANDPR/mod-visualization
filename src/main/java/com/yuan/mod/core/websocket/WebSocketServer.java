package com.yuan.mod.core.websocket;


import com.yuan.mod.core.rxtx.SerialConnect;
import com.yuan.mod.core.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 处理 WebSocket 连接相关的操作，包括连接建立、关闭、接收消息及错误处理
 */
@EnableScheduling
@Component
@ServerEndpoint("/websocket") //暴露的ws应用的路径
@Slf4j
public class WebSocketServer {
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    public static Session session;

    private final SerialConnect serialConnect = SpringContextUtils.getBean(SerialConnect.class);

    public static void sendMessage(String message) throws IOException {
        WebSocketServer.session.getAsyncRemote().sendText(message);
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        WebSocketServer.session = session;
        startSerialListener();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {

    }

    /**
     * 收到客户端消息后调用的方法
     **/
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 发生异常调用方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
    }


    /**
     * 启动串口监听
     */
    private void startSerialListener() {
        int i = serialConnect.startComPort();
        if (i == 1) {
            // 启动线程来处理收到的数据
            serialConnect.start();
            try {
                String st = "\r";
                log.info("发出字节数：" + st.getBytes("gbk").length);
                serialConnect.getOutputStream().write(st.getBytes("gbk"), 0,
                        st.getBytes("gbk").length);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
