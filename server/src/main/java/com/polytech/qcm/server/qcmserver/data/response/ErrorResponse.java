package com.polytech.qcm.server.qcmserver.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private String error;
  private String message;
  private String timestamp;
  private String path;

}
