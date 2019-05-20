package com.polytech.qcm.server.qcmserver.repository;

import com.polytech.qcm.server.qcmserver.security.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

public class FakeUserDetailsRepository implements UserDetailsRepository {

  private final List<UserDetailsImpl> users;

  public FakeUserDetailsRepository(List<UserDetailsImpl> users) {
    this.users = users;
  }

  @Override
  public Optional<UserDetailsImpl> findByUsername(String username) {
    return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
  }
}
