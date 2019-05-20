package com.polytech.qcm.server.qcmserver.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {

  private Long id; // TODO use JPA annotion for MySQL
  private String username;
  private String password; //should be already hashed

  public User(String username, String password) {
    this(0L, username, password);
  }

}
