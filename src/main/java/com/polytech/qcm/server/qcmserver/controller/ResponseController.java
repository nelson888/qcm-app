package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import com.polytech.qcm.server.qcmserver.service.MessageSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/response")
public class ResponseController {
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;
  private final ResponseRepository responseRepository;
  private final MessageSender messageSender;

  public ResponseController(ChoiceRepository choiceRepository,
                            UserRepository userRepository,
                            ResponseRepository responseRepository,
                            MessageSender messageSender) {
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
    this.responseRepository = responseRepository;
    this.messageSender = messageSender;
  }

  //TODO check if the given choice is for the current question
  @PostMapping("/")
  @ResponseBody
  public ResponseEntity postResponse(Principal user, @RequestBody Choice c) {
    Choice choice = choiceRepository.findById(c.getId()).orElseThrow(() -> new BadRequestException("Choice with id " + c.getId() + " doesn't exists"));
    String username = user.getName();
    Question question = choice.getQuestion();
    Response existingAnswer = responseRepository.findByUser_UsernameAndChoice_Question_Id(username, question.getId());
    if (existingAnswer != null) {
      throw new BadRequestException("User " + username + " has already answered question " + question);
    }
    Response response = new Response(userRepository.findByUsername(user.getName()).get(), choice);
    responseRepository.saveAndFlush(response);

    return ResponseEntity.ok(response);
  }
}
