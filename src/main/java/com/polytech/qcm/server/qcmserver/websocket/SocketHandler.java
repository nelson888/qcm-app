package com.polytech.qcm.server.qcmserver.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.qcm.server.qcmserver.data.response.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

  private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message)
    throws InterruptedException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    for(WebSocketSession webSocketSession : sessions) {
      Message m = new Message("test", "test");
     // Map value = new Gson().fromJson(message.getPayload(), Map.class);
      webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(m)));
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    //the messages will be broadcasted to all users.
    sessions.add(session);
  }
}
