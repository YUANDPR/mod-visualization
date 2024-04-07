package com.yuan.mod.controller.websocket;



import com.yuan.mod.controller.rxtx.SerialConnect;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Package com.yuan
 * @ClassName a
 * @Description TODO
 * @Author YUAND
 * @Date 2024/2/22 21:59
 * @Version 1.0
 */

@EnableScheduling
@Component
@ServerEndpoint("/websocket") //暴露的ws应用的路径
public class WebSocketServer {


    /**concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。*/
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    public static Session session;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        WebSocketServer.session = session;
        SerialConnect serialConnect = new SerialConnect();
        serialConnect.go();
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
    public static void sendMessage(String message) throws IOException {
        WebSocketServer.session.getAsyncRemote().sendText(message);
    }



}
