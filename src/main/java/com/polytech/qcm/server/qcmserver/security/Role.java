package com.polytech.qcm.server.qcmserver.security;

public enum Role {
  TEACHER, STUDENT, ADMIN;

  public String roleName() {
    return "ROLE_" + name();
  }
}
