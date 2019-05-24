package com.polytech.qcm.server.qcmserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceIds {

  private Set<Integer> ids;

}
