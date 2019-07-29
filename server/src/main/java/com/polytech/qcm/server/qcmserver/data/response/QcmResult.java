package com.polytech.qcm.server.qcmserver.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QcmResult {

  private List<String> participants; // username
  List<QuestionResult> questionResults;

}
