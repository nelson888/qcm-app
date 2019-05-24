package com.polytech.qcm.server.qcmserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.Principal;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class PrincipalUser implements Principal {

  private String name;
}
