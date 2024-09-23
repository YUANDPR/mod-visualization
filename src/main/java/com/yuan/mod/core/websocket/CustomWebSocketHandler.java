package com.yuan.mod.core.websocket;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            session.close();  // 关闭未认证的连接
            return;
        }
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理 WebSocket 消息
        System.out.println("Received message: " + message.getPayload());
    }
}