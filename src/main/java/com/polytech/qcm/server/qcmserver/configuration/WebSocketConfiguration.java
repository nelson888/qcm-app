package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    MessageHandler studentsSessions = new MessageHandler(new CopyOnWriteArraySet<>());
    MessageHandler teachersSessions = new MessageHandler(new CopyOnWriteArraySet<>());
    registry.addHandler(studentsSessions, "/auth/session/student");
    registry.addHandler(teachersSessions, "/auth/session/teacher");
  }

}
