package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.ChoiceIds;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.exception.ForbiddenRequestException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/response")
public class ResponseController {
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;
  private final ResponseRepository responseRepository;
  private final QcmRepository qcmRepository;
  private final Map<Integer, Integer> currentQuestionMap;
  private final MessageSender messageSender;

  public ResponseController(ChoiceRepository choiceRepository,
                            UserRepository userRepository,
                            ResponseRepository responseRepository,
                            QcmRepository qcmRepository, MessageSender messageSender,
                            Map<Integer, Integer> currentQuestionMap) {
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
    this.responseRepository = responseRepository;
    this.qcmRepository = qcmRepository;
    this.currentQuestionMap = Collections.unmodifiableMap(currentQuestionMap);
    this.messageSender = messageSender;
  }

  @PostMapping("/")
  @ResponseBody
  public ResponseEntity postResponse(Principal user, @RequestBody ChoiceIds cIds) {
    List<Response> responses = new ArrayList<>();
    for (int id : cIds.getIds()) {
      Choice choice = choiceRepository.findById(id).orElseThrow(() -> new BadRequestException("Choice with id " + id + " doesn't exists"));
      checkCanAnswer(choice);
      String username = user.getName();
      Question question = choice.getQuestion();
      Response existingAnswer = responseRepository.findByUser_UsernameAndChoice_Question_Id(username, question.getId());
      if (existingAnswer != null) {
        throw new BadRequestException("User " + username + " has already answered question " + question);
      }
      responses.add(new Response(userRepository.findByUsername(user.getName()).get(), choice));
    }

    responses = responseRepository.saveAll(responses);
    responseRepository.flush();
    return ResponseEntity.ok(responses);
  }

  //can answer only if the choice is for a current question
  private void checkCanAnswer(Choice c) {
    QCM qcm = qcmRepository.findById(c.getQuestion().getQcm().getId()).get();
    Integer questionIndex = currentQuestionMap.get(qcm.getId());
    if (questionIndex == null) {
      throw new ForbiddenRequestException("The response given doesn't correspond to any current question");
    }
    Question currentQuestion = qcm.getQuestions().get(questionIndex);
    if (!currentQuestion.equals(c.getQuestion())) {
      throw new ForbiddenRequestException("Question '" + currentQuestion.getQuestion() + "' is not a current question");
    }
  }

}
