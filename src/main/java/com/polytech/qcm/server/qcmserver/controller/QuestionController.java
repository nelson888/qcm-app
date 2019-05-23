package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/question")
public class QuestionController {


  private final ResponseRepository responseRepository;
  private final ChoiceRepository choiceRepository;

  public QuestionController(ResponseRepository responseRepository,
                            ChoiceRepository choiceRepository) {
    this.responseRepository = responseRepository;
    this.choiceRepository = choiceRepository;
  }


  @GetMapping("/{id}/responses")
  public ResponseEntity getAllResponses(@PathVariable("id") int id) {
    List<Choice> choices = choiceRepository.findAllByQuestion_Id(id);
    List<Response> responses = choices.stream()
      .flatMap(c -> responseRepository.findAllByChoice_Id(c.getId()).stream())
      .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

}
