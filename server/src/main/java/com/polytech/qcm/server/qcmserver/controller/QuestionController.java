package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.exception.ForbiddenRequestException;
import com.polytech.qcm.server.qcmserver.exception.NotFoundException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;

import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/question")
@Api(value = "Controller to access data about a question")
public class QuestionController {


  private final QuestionRepository questionRepository;
  private final ResponseRepository responseRepository;
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;

  public QuestionController(QuestionRepository questionRepository,
                            ResponseRepository responseRepository,
                            ChoiceRepository choiceRepository,
                            UserRepository userRepository) {
    this.questionRepository = questionRepository;
    this.responseRepository = responseRepository;
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
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

  @GetMapping("/{id}/hasAnswered")
  @ApiOperation(value = "Get whether the current user has responded to the question with the given question id", response = Boolean.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved result"),
    @ApiResponse(code = 404, message = "Question with given id not found"),
    @ApiResponse(code = 403, message = "You are not authenticated or you are a teacher"),
  })
  public ResponseEntity hasAnswered(@PathVariable("id") int id, Principal principal) {
    Question question = questionRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(String.format("Question with id %d was not found", id)));
    User user = userRepository.findByUsername(principal.getName()).get();
    if (Role.TEACHER.equals(user.getRole())) {
      throw new ForbiddenRequestException("A teacher can't respond to MCQs");
    }
    return ResponseEntity.ok(!responseRepository.findAllByUser_UsernameAndChoice_Question_Id(principal.getName(), question.getId()).isEmpty());
  }

}
