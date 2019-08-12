package com.polytech.qcm.server.qcmserver.data.response;

import com.polytech.qcm.server.qcmserver.data.Question;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class QuestionResult {

  private Question question;
  private Map<String, Boolean> responses; // username =>  vrai si reponse juste, faux sinon


}
