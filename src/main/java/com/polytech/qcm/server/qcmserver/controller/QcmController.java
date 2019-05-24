package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.data.response.QcmResult;
import com.polytech.qcm.server.qcmserver.data.response.QuestionResult;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.exception.ForbiddenRequestException;
import com.polytech.qcm.server.qcmserver.exception.NotFoundException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/qcm")
@Api(value = "Controller to manipulates qcms")
public class QcmController {

  private static final Logger LOGGER = LoggerFactory.getLogger(QcmController.class);

  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final ResponseRepository responseRepository;
  private final UserRepository userRepository;
  private final Map<Integer, Integer> currentQuestionMap; //map qcmId => question INDEX (not id)

  public QcmController(QcmRepository qcmRepository,
                       QuestionRepository questionRepository,
                       ResponseRepository responseRepository, UserRepository userRepository,
                       Map<Integer, Integer> currentQuestionMap) {
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.responseRepository = responseRepository;
    this.userRepository = userRepository;
    this.currentQuestionMap = currentQuestionMap;
  }

  @GetMapping("/all")
  @ApiOperation(value = "View the list of all qcm", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved list"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
  })
  public ResponseEntity getAll() {
    return ResponseEntity.ok(qcmRepository.findAll());
  }

  @GetMapping("/mines")
  @ApiOperation(value = "View the list of QCMs created by the given user", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved list"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
  })
  public ResponseEntity getMyQcms(Principal principal) {
    return ResponseEntity.ok(qcmRepository.findAllByAuthor_Username(principal.getName()));
  }


  @GetMapping("/{id}")
  @ApiOperation(value = "View a qcm with a given id", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved qcm"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity getById(Principal principal, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);

    if (isStudent(principal)) { //if is student, we have to hide answer choices
      hideAnswers(qcm);
    }
    return ResponseEntity.ok(qcm);
  }



  @GetMapping("/new")
  @ApiOperation(value = "Creates a new qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully created qcm"),
    @ApiResponse(code = 403, message = "You are not a teacher"),
  })
  public ResponseEntity newQvm(Principal principal) {
    QCM qcm = new QCM("",
      getUser(principal), State.INCOMPLETE, Collections.emptyList());
    LOGGER.info("User {} created a new qcm", principal.getName());
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Updates a qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully updated qcm"),
    @ApiResponse(code = 400, message = "The qcm is malformed"),
    @ApiResponse(code = 403, message = "You are the owner of this qcm"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity save(Principal principal, @RequestBody QCM newQcm, @PathVariable("id") int id) {
    for(Question question: newQcm.getQuestions()){
      if (question.getChoices().stream().noneMatch(Choice::isAnswer)) {
        throw new BadRequestException("Question with id " + id + " has no good answer");
      }
    }

    User user = getUser(principal);
    QCM qcm = getQcm(id);
    qcm.setAuthor(user);
    qcm.setState(State.COMPLETE);
    if (newQcm.getName() != null) {
      qcm.setName(newQcm.getName());
    }
    //delete old questions
    questionRepository.deleteAll(qcm.getQuestions());

    //store new questions in jpa
    List<Question> questions = newQcm.getQuestions();
    for (Question question : questions) {
      question.setQcm(qcm);
      question.getChoices().forEach(c -> c.setQuestion(question));
    }
    questions = newQcm.getQuestions();
    qcm.setQuestions(questions);

    return ResponseEntity.ok(qcmRepository.save(qcm));
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Deletes a qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully deleted qcm"),
    @ApiResponse(code = 403, message = "You are not the owner of this qcm"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity delete(Principal user, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);
    checkRights(user, qcm);

    List<Response> qcmResponses =  qcm.getQuestions()
      .stream()
      .flatMap(q -> responseRepository.findAllByChoice_Question_Id(q.getId()).stream())
      .collect(Collectors.toList());
    responseRepository.deleteAll(qcmResponses);
    qcmRepository.deleteById(id);

    LOGGER.info("User {} deleted qcm with id {}", user.getName(), id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/{id}/launch")
  @ApiOperation(value = "Launches a qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully launched qcm"),
    @ApiResponse(code = 403, message = "You are not the owner of this qcm"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity launchQCM(Principal user, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);
    checkRights(user, qcm);
    qcm.setState(State.STARTED);
    currentQuestionMap.put(id, 0);
    LOGGER.info("User {} launched qcm with id {}", user.getName(), id);
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @GetMapping("/{id}/finish")
  @ApiOperation(value = "Ends a qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully ended qcm"),
    @ApiResponse(code = 403, message = "You are not the owner of this qcm"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity finishQCM(Principal user, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);
    checkRights(user, qcm);
    qcm.setState(State.FINISHED);
    LOGGER.info("User {} ended qcm with id {}", user.getName(), id);
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @GetMapping("/{id}/currentQuestion")
  @ApiOperation(value = "View the current question of a qcm", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully returned the current question"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity getCurrentQuestion(Principal user, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);
    Integer questionIndex = currentQuestionMap.get(id);
    if (questionIndex == null){
      throw new BadRequestException("The qcm has not started");
    }
    if (isStudent(user)) {
      hideAnswers(qcm);
    }
    return ResponseEntity.ok(qcm.getQuestions().get(questionIndex));
  }

  @GetMapping("/{id}/nextQuestion")
  @ApiOperation(value = "Pass to the next question", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully passed to the next question"),
    @ApiResponse(code = 403, message = "You are not the owner of this qcm"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found or there is no next question")
  })
    public ResponseEntity nextQuestion(Principal user, @PathVariable("id") int id){
      QCM qcm = getQcm(id);
    checkRights(user, qcm);
    List<Question> questions = qcm.getQuestions();
    int questionIndex = currentQuestionMap.get(id);
    if (questionIndex >= questions.size() - 1) {
      qcm.setState(State.FINISHED);
      qcmRepository.saveAndFlush(qcm);
      currentQuestionMap.remove(id);
      throw new NotFoundException("There is no next question");
    } else {
      currentQuestionMap.put(id, questionIndex + 1);
      Question question = questions.get(currentQuestionMap.get(id));
      return ResponseEntity.ok(question);
    }
  }

  @GetMapping("/{id}/result")
  @ApiOperation(value = "Get the result of a qcm. A teacher can see all responses but a student can only see his result", response = QCM.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully view the result"),
    @ApiResponse(code = 403, message = "You are not authenticated"),
    @ApiResponse(code = 404, message = "The qcm you were trying to reach is not found")
  })
  public ResponseEntity qcmResult(Principal user, @PathVariable("id") int id) {
    QCM qcm = getQcm(id);
    String username = user.getName();
    QcmResult result = toResult(qcm);
    if (isStudent(user)) {
      result.getParticipants().clear();
      result.getParticipants().add(username);
      for (QuestionResult qr : result.getQuestionResults()) {
        Map<String, Boolean> responses = qr.getReponses();
        Boolean response = responses.get(username);
        responses.clear();
        responses.put(username, response);
      }
    }
    return ResponseEntity.ok(result);
  }

  private QcmResult toResult(QCM qcm) {
    List<String> participants = userRepository.findAllByRole(Role.STUDENT.roleName())
      .stream()
      .map(User::getUsername)
      .collect(Collectors.toList());
    List<QuestionResult> qrs = qcm.getQuestions()
      .stream()
      .map(this::toResult)
      .collect(Collectors.toList());
    return new QcmResult(participants, qrs);
  }

  private QuestionResult toResult(Question question) {
    Set<Choice> rightAnswers = question.getChoices().stream().filter(Choice::isAnswer).collect(Collectors.toSet());
    List<Response> responses = responseRepository.findAllByChoice_Question_Id(question.getId()); // get all responses for the given question
    Map<User, Set<Choice>> userResponsesMap = new HashMap<>(); // map to split choices by users
    for (Response r: responses) {
      Set<Choice> userResponses = userResponsesMap.computeIfAbsent(r.getUser(), (k) -> new HashSet<>());
      userResponses.add(r.getChoice());
    }
    Map<String, Boolean> responsesMap = new HashMap<>();

    userResponsesMap
      .forEach((User u, Set<Choice> userChoices) -> responsesMap.put(u.getUsername(), rightAnswers.equals(userChoices)));

    return new QuestionResult(question, responsesMap);
  }

  private void checkRights(Principal user, QCM qcm) {
    if (!user.getName().equals(qcm.getAuthor().getUsername())) {
      throw new ForbiddenRequestException("You cannot access this QCM: it is not yours!");
    }
  }

  private User getUser(Principal principal) {
    return userRepository.findByUsername(principal.getName()).get();
  }

  private boolean isStudent(Principal principal) {
    User user = getUser(principal);
    return Role.STUDENT.equals(user.getRole());
  }

  private void hideAnswers(QCM qcm) {
    for(Question question:qcm.getQuestions()){
      for (Choice choice:question.getChoices()){
        choice.setAnswer(false);
      }
    }
  }

  private QCM getQcm(int id) {
    return qcmRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("Qcm with id " + id + " doesn't exist"));
  }
}
