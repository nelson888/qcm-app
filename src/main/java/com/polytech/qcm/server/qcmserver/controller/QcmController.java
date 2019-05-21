package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.response.ErrorResponse;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  private final QcmRepository qcmRepository;

  public QcmController(QcmRepository qcmRepository) {
    this.qcmRepository = qcmRepository;
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

  @PostMapping("/qcm/{id}")
  public ResponseEntity save(@PathVariable("id") int id, @RequestBody QCM qcm){
    qcm.setId(id);
    qcmRepository.save(qcm);
    return ResponseEntity.ok(qcm);
  }

  @DeleteMapping("/qcm/{id}")
  public ResponseEntity delete(@PathVariable("id") int id){
    qcmRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/qcm/{id}/launch")
  public ResponseEntity launchQCM(@PathVariable("id") int id) {
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    qcm.setState(State.STARTED);
    return ResponseEntity.ok(qcm);
  }

  @GetMapping("/qcm/{id}/next")
    public ResponseEntity nextQuestion(@PathVariable("id") int id){ //TODO implement going to teh next question
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
      //TODO
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
