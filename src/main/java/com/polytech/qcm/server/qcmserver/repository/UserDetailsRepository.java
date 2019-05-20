package com.polytech.qcm.server.qcmserver.repository;

import com.polytech.qcm.server.qcmserver.security.UserDetailsImpl;

import java.util.Optional;

public interface UserDetailsRepository {

  Optional<UserDetailsImpl> findByUsername(String username);
}
