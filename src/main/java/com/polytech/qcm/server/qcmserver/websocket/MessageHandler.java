package com.polytech.qcm.server.qcmserver.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;

public class MessageHandler extends TextWebSocketHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

  private final Set<WebSocketSession> webSocketSessions;

  public MessageHandler(Set<WebSocketSession> webSocketSessions) {
    this.webSocketSessions = webSocketSessions;
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    LOGGER.info("Session {} was closed", session);
    webSocketSessions.remove(session);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    LOGGER.info("Session {} was established", session);
    webSocketSessions.add(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    LOGGER.info("A message was received: {}", message.getPayload());
  }

}
