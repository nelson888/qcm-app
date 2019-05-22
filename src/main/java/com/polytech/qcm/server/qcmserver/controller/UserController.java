package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/teachers")
  public ResponseEntity getTeachers(){
    return ResponseEntity.ok(userRepository.findAllByRole(Role.TEACHER));
  }

  @GetMapping("/students")
  public ResponseEntity getStudents(){
    return ResponseEntity.ok(userRepository.findAllByRole(Role.STUDENT));
  }

  @GetMapping("/everyone")
  public ResponseEntity getEveryone(){
    return ResponseEntity.ok(userRepository.findAll());
  }
}
