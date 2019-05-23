package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.response.ErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AppErrorController implements ErrorController  {

  public static final String ERROR_PATH = "/error";

  private final ErrorAttributes errorAttributes;

  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  @RequestMapping(value = ERROR_PATH)
  public ResponseEntity error(HttpServletRequest request) {
    Map<String, String> errorAttributes = getErrorAttributes(request);
    HttpStatus status = getStatus(request);
    return ResponseEntity.status(status)
      .body(new ErrorResponse(errorAttributes.get("error"),
        errorAttributes.get("message"),
        errorAttributes.get("timestamp"),
        errorAttributes.get("path")));
  }

  private Map<String, String> getErrorAttributes(HttpServletRequest request) {
    ServletWebRequest servletWebRequest = new ServletWebRequest(request);
    Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, false);
    Map<String, String> map = new HashMap<>();
    errorAttributes.forEach((String key, Object value)-> map.put(key, value.toString()));
    return map;
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request
      .getAttribute("javax.servlet.error.status_code");
    if (statusCode != null) {
      try {
        return HttpStatus.valueOf(statusCode);
      }
      catch (Exception ex) {
      }
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
