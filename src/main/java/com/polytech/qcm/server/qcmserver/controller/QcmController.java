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

import java.security.Principal;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;

  public QcmController(QcmRepository qcmRepository,
                       QuestionRepository questionRepository,
                       ChoiceRepository choiceRepository,
                       UserRepository userRepository) {
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  public ResponseEntity getById( @PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
   // checkRights(principal, qcm);
    return ResponseEntity.ok(qcm);
  }

  @PostMapping("/")
  public ResponseEntity save(Principal principal, @RequestBody QCM qcm) {
    User user = userRepository.findByUsername(principal.getName()).get();
    qcm.setAuthor(user);
    qcm.setState(State.INCOMPLETE);
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
    return ResponseEntity.ok(qcmRepository.saveAndFlush(qcm));
  }

  @GetMapping("/{id}/nextQuestion")
    public ResponseEntity nextQuestion(Principal user, @PathVariable("id") int id){ //TODO implement going to teh next question
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    checkRights(user, qcm);
      //TODO
    Question question = qcm.getQuestions().get(0);
    return ResponseEntity.ok(question); //TODO retourner la nouvelle question en cours
  }

  private void checkRights(Principal user, QCM qcm) {
    if (!user.getName().equals(qcm.getAuthor().getUsername())) {
      throw new ForbiddenRequestException("You cannot access this QCM: it is not yours!");
    }
  }
}
