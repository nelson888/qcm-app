package com.polytech.qcm.server.qcmserver.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidJwtAuthenticationException extends BadCredentialsException {
  public InvalidJwtAuthenticationException(String msg, Throwable e) {
    super(msg, e);
  }
}
