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
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final ChoiceRepository choiceRepository;
  private final ResponseRepository responseRepository;
  private final UserRepository userRepository;
  private final Map<Integer, Integer> currentQuestionMap; //map qcmId => question INDEX (not id)

  public QcmController(QcmRepository qcmRepository,
                       QuestionRepository questionRepository,
                       ChoiceRepository choiceRepository,
                       ResponseRepository responseRepository, UserRepository userRepository,
                       Map<Integer, Integer> currentQuestionMap) {
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.choiceRepository = choiceRepository;
    this.responseRepository = responseRepository;
    this.userRepository = userRepository;
    this.currentQuestionMap = currentQuestionMap;
  }

  @GetMapping("/all")
  public ResponseEntity getAll() {
    return ResponseEntity.ok(qcmRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity getById(Principal principal, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
    User user = getUser(principal);
    if (!isTeacher(principal)){
      for(Question question:qcm.getQuestions()){
        for (Choice choice:question.getChoices()){
          choice.setAnswer(false);
        }
      }
    }
    return ResponseEntity.ok(qcm);
  }



  @GetMapping("/new")
  public ResponseEntity newQvm(Principal principal) {
    QCM qcm = new QCM("",
      getUser(principal), State.INCOMPLETE, Collections.emptyList());
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @PutMapping("/{id}")
  public ResponseEntity save(Principal principal, @RequestBody QCM newQcm, @PathVariable("id") int id) {
    for(Question question: newQcm.getQuestions()){
      if (question.getChoices().stream().noneMatch(Choice::isAnswer)) {
        throw new BadRequestException("Question with id " + id + " has no good answer");
      }
    }

    User user = getUser(principal);
    QCM qcm = qcmRepository.findById(id).orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
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
    questions = questionRepository.saveAll(newQcm.getQuestions());
    qcm.setQuestions(questions);

    return ResponseEntity.ok(qcmRepository.save(qcm));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(Principal user, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);

    qcmRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/{id}/launch")
  public ResponseEntity launchQCM(Principal user, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);
    qcm.setState(State.STARTED);
    currentQuestionMap.put(id, 0);
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @GetMapping("/{id}/finish")
  public ResponseEntity finishQCM(Principal user, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);
    qcm.setState(State.FINISHED);
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @GetMapping("/{id}/currentQuestion")
  public ResponseEntity getCurrentQuestion(Principal user, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
    Integer questionIndex = currentQuestionMap.get(id);
    if (questionIndex == null){
      throw new BadRequestException("The qcm has not started");
    }
    if (!isTeacher(user)){
      for(Question question:qcm.getQuestions()){
        for (Choice choice:question.getChoices()){
          choice.setAnswer(false);
        }
      }
    }
    return ResponseEntity.ok(qcm.getQuestions().get(questionIndex));
  }

  @GetMapping("/{id}/nextQuestion")
    public ResponseEntity nextQuestion(Principal user, @PathVariable("id") int id){
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
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
  public ResponseEntity qcmResult(Principal user, @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);
    return ResponseEntity.ok(toResult(qcm));
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
    List<Response> responses = responseRepository.findAllByChoice_Question_Id(question.getId());
    Map<String, Boolean> responsesMap = new HashMap<>();
    for (Response response : responses) {
      responsesMap.put(response.getUser().getUsername(), response.getChoice().isAnswer());
    }
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
  private boolean isTeacher(Principal principal) {
    User usr = getUser(principal);
    if (usr.getRole().equals(Role.TEACHER.roleName())){
      return true;
    }
    return false;
  }
}
