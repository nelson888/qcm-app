package com.polytech.qcm.server.qcmserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Service
public class MessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

  private final Set<WebSocketSession> studentsSessions;
  private final Set<WebSocketSession> teachersSessions;

  public MessageSender(Set<WebSocketSession> studentsSessions, Set<WebSocketSession> teachersSessions) {
    this.studentsSessions = studentsSessions;
    this.teachersSessions = teachersSessions;
  }


  public void sendToStudents(String message) {
    sendTo(studentsSessions, message);
  }

  public void sendToTeacher(String message) {
    sendTo(teachersSessions, message);
  }

  public void sendToEverybody(String message) {
    sendToStudents(message);
    sendToStudents(message);
  }

  public void sendTo(Set<WebSocketSession> sessions, String message) {
    for (WebSocketSession s : sessions) {
      try {
        s.sendMessage(new TextMessage(message));
      } catch (IOException e) {
        LOGGER.error("Error while sending message to {}", s, e);
      }
    }
  }
  //prof passz a la question suivante => Tout les eleves

  //eleve repond a une question (mettre a jour grille du prof) => prof

  //
}
