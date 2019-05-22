package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  //TODO faire un endpoint GET  '/teachers' qui retourne la liste des teacher,
  //TODO, un autre  GET qui retourne '/students' la liste des etudiants
  //TODO, un autre  GET qui retourne tout le monede
}
