package com.polytech.qcm.server.qcmserver.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

  private String type;
  private Object data;

}
