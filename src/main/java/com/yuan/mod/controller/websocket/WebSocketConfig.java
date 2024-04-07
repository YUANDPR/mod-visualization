package com.yuan.mod.controller.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter; /**
 * @Package com.yuan
 * @ClassName a
 * @Description TODO
 * @Author YUAND
 * @Date 2024/2/22 22:03
 * @Version 1.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
