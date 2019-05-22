package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.exception.ForbiddenRequestException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;
  private final Map<Integer, Integer> currentQuestionMap; //map qcmId => question INDEX (not id)

  public QcmController(QcmRepository qcmRepository,
                       QuestionRepository questionRepository,
                       ChoiceRepository choiceRepository,
                       UserRepository userRepository,
                       Map<Integer, Integer> currentQuestionMap) {
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
    this.currentQuestionMap = currentQuestionMap;
  }

  @GetMapping("/all")
  public ResponseEntity getAll() {
    return ResponseEntity.ok(qcmRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity getById(@PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
    // checkRights(principal, qcm);
    return ResponseEntity.ok(qcm);
  }

  @GetMapping("/{id}/currentQuestion")
  public ResponseEntity getCurrentQuestion( @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
    Integer questionIndex = currentQuestionMap.get(id);
    if (questionIndex == null){
      throw new BadRequestException("The qcm has not started");
    }
    else {
      return ResponseEntity.ok(qcm.getQuestions().get(questionIndex));
    }
  }

  @GetMapping("/new")
  public ResponseEntity newQvm(Principal principal) {
    QCM qcm = new QCM();
    User user = userRepository.findByUsername(principal.getName()).get();
    qcm.setAuthor(user);
    qcm.setState(State.INCOMPLETE);
    return ResponseEntity.ok(qcm);
  }

  @PostMapping("/")
  public ResponseEntity save(Principal principal, @RequestBody QCM qcm) {
    User user = userRepository.findByUsername(principal.getName()).get();
    qcm.setAuthor(user);
    qcm.setState(State.COMPLETE);
    for (Question question : qcm.getQuestions()) {
      question.setQcm(qcm);
      for (Choice choice : question.getChoices()) {
        choice.setQuestion(question);
      }
    }

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

  @GetMapping("/{id}/nextQuestion")
    public ResponseEntity nextQuestion(Principal user, @PathVariable("id") int id){
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);
    if (currentQuestionMap.get(id) == qcm.getQuestions().size()-1){
      return ResponseEntity.ok("END OF QCM");
    }
    else{
      currentQuestionMap.put(id, currentQuestionMap.get(id)+1);
      Question question = qcm.getQuestions().get(currentQuestionMap.get(id));
      return ResponseEntity.ok(question);
    }
  }

  private void checkRights(Principal user, QCM qcm) {
    if (!user.getName().equals(qcm.getAuthor().getUsername())) {
      throw new ForbiddenRequestException("You cannot access this QCM: it is not yours!");
    }
  }
}
