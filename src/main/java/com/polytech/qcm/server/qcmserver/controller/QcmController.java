package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.ErrorResponse;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/qcm")
public class QcmController {

  @Autowired
  private QcmRepository qcmRepository;

  @GetMapping("/")
  public ResponseEntity test() {
    return ResponseEntity.ok(new ErrorResponse("test", "test"));
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

  @PostMapping("/qcm/save")
  public ResponseEntity save(@RequestBody QCM qcm){
    qcmRepository.save(qcm);
    return ResponseEntity.ok(qcm);
  }
  @DeleteMapping("/qcm/{id}/delete")
  public ResponseEntity delete(@PathVariable("id") int id){
    qcmRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/qcm/{id}/launch")
  public ResponseEntity launchQCM(@PathVariable("id") int id){
    QCM qcm = qcmRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    qcm.setState(State.STARTED);
    return ResponseEntity.ok(qcm);
  }

  @GetMapping("/qcm/{id}/next")
    public ResponseEntity nextQuestion(@PathVariable("id") int id){ //TODO implement going to teh next question
    /*
      QCM qcm = qcmRepository.findById(id)
              .orElseThrow(() -> new BadRequestException("Qcm with id " + id + " doesn't exist"));
    */
  }

}
