package com.polytech.qcm.server.qcmserver.data;

public enum Role {
  TEACHER, STUDENT, ADMIN;

  public String roleName() {
    return "ROLE_" + name();
  }
}
