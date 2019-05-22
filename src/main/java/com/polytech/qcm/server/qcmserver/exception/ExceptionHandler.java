package com.polytech.qcm.server.qcmserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

  /*
  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<ErrorResponse> badRequestException(BadRequestException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(new ErrorResponse("Bad request", ex.getMessage()));
  }*/
}
