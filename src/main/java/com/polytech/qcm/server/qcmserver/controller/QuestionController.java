package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/question")
public class QuestionController {


  private final ResponseRepository responseRepository;
  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;
  private final ChoiceRepository choiceRepository;

  public QuestionController(ResponseRepository responseRepository,
                            QuestionRepository questionRepository,
                            UserRepository userRepository,
                            ChoiceRepository choiceRepository) {
    this.responseRepository = responseRepository;
    this.questionRepository = questionRepository;
    this.userRepository = userRepository;
    this.choiceRepository = choiceRepository;
  }

  @PostMapping("/{id}")
  @ResponseBody
  public ResponseEntity postResponse(@PathVariable("id") int id, Principal user, @RequestBody Choice c) {
    Response response = new Response();
    Choice choice = choiceRepository.findById(c.getId()).orElseThrow(() -> new BadRequestException("Choice with id " + c.getId() + " doesn't exists"));
    choice.setQuestion(questionRepository.findById(id).orElseThrow(() -> new BadRequestException("Question with id " + id + " doesn't exists")));
    response.setUser(userRepository.findByUsername(user.getName()).get());
    response.setChoice(choice);
    responseRepository.saveAndFlush(response);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
