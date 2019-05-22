package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;

  public QcmController(QcmRepository qcmRepository, QuestionRepository questionRepository) {
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
  }

  @GetMapping("/{id}")
  public ResponseEntity getById(@PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exists"));
    return ResponseEntity.ok(qcm);
  }

  @PostMapping("/new")
  public ResponseEntity newQcm() {
    QCM qcm = new QCM();
//    qcmRepository.save(qcm); TODO bug
    return ResponseEntity.status(HttpStatus.CREATED).body(qcm);
  }

  @PostMapping("/qcm/")
  public ResponseEntity save(@RequestBody QCM qcm) {
    QCM savedQcm = qcmRepository.save(qcm);
    qcm.getQuestions().forEach(q -> q.setQcm(savedQcm));
    questionRepository.saveAll(qcm.getQuestions());

    return ResponseEntity.ok(qcm);
  }

  @DeleteMapping("/qcm/{id}")
  public ResponseEntity delete(@PathVariable("id") int id){
    QCM qcm = qcmRepository.findById(id)
      .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    qcmRepository.deleteById(id);
    questionRepository.deleteAll(qcm.getQuestions());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/qcm/{id}/launch")
  public ResponseEntity launchQCM(@PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    qcm.setState(State.STARTED);
    return ResponseEntity.ok(qcmRepository.save(qcm));
  }

  @GetMapping("/qcm/{id}/next")
    public ResponseEntity nextQuestion(@PathVariable("id") int id){ //TODO implement going to teh next question
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
      //TODO
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
