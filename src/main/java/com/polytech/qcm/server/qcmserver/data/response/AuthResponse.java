package com.polytech.qcm.server.qcmserver.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthResponse {

  private String username;
  private String role;
  private String jwt;

}
