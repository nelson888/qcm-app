package com.polytech.qcm.server.qcmserver.exception;

public class InvalidJwtAuthenticationException extends BadCredentialsException {
  public InvalidJwtAuthenticationException(String msg, Throwable e) {
    super(msg, e);
  }
}
