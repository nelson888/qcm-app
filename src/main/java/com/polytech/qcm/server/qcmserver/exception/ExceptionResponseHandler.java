package com.polytech.qcm.server.qcmserver.exception;

import com.polytech.qcm.server.qcmserver.data.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<ErrorResponse> badRequestException(BadRequestException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Bad request", ex.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public final ResponseEntity<ErrorResponse> authenticationException(BadCredentialsException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorResponse("Bad authentication", ex.getMessage()));
  }

  @ExceptionHandler(InvalidJwtAuthenticationException.class)
  public final ResponseEntity<ErrorResponse> authenticationException(InvalidJwtAuthenticationException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorResponse("Bad authentication", ex.getMessage()));
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public final ResponseEntity<ErrorResponse> authenticationException(UsernameNotFoundException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(new ErrorResponse("Username was not found", ex.getMessage()));
  }

}
