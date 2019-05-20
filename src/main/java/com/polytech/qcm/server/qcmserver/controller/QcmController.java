package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qcm")
public class QcmController {


  @GetMapping("/")
  public ResponseEntity test() {
    return ResponseEntity.ok(new ErrorResponse("test", "test"));
  }
}
