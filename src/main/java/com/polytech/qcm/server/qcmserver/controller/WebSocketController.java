package com.polytech.qcm.server.qcmserver.controller;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {

  private final SimpMessageSendingOperations messagingTemplate;

  public WebSocketController(SimpMessageSendingOperations messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/message")
  @SendToUser("/queue/reply")
  public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
    String name = principal.getName();
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", name);
    return name;
  }

  @MessageExceptionHandler
  @SendToUser("/queue/errors")
  public String handleException(Throwable exception) {
    return exception.getMessage();
  }

  @Scheduled(fixedRate = 5000)
  public void greeting() {
    try {
      Thread.sleep(1000); // simulated delay
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("scheduled");
    this.messagingTemplate.convertAndSend("/topic/greetings", "Hello");
  }

}