package com.polytech.qcm.server.qcmserver.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {

  public ForbiddenRequestException(String message) {
    super(message);
  }

}
