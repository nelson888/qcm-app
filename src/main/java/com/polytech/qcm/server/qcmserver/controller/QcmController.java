package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.ErrorResponse;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/new")
  public ResponseEntity newQcm() {
    QCM qcm = new QCM();
//    qcmRepository.save(qcm); TODO bug
    return ResponseEntity.status(HttpStatus.CREATED).body(qcm);
  }

}
