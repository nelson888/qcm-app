package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/question")
@Api(value = "Controller to access data about a question")
public class QuestionController {


  private final ResponseRepository responseRepository;
  private final ChoiceRepository choiceRepository;

  public QuestionController(ResponseRepository responseRepository,
                            ChoiceRepository choiceRepository) {
    this.responseRepository = responseRepository;
    this.choiceRepository = choiceRepository;
  }


  @GetMapping("/{id}/responses")
  @ApiOperation(value = "View the list of responses for a given question", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved list"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
  })
  public ResponseEntity getAllResponses(@PathVariable("id") int id) {
    List<Choice> choices = choiceRepository.findAllByQuestion_Id(id);
    List<Response> responses = choices.stream()
      .flatMap(c -> responseRepository.findAllByChoice_Id(c.getId()).stream())
      .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

}
