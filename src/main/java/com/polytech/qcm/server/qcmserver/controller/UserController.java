package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/teachers")
  public ResponseEntity getTeachers() {
    return ResponseEntity.ok(toUsername(userRepository.findAllByRole(Role.TEACHER.roleName())));
  }

  @GetMapping("/students")
  public ResponseEntity getStudents(){
    return ResponseEntity.ok(toUsername(userRepository.findAllByRole(Role.STUDENT.roleName())));
  }

  @GetMapping("/all")
  public ResponseEntity getEveryone(){
    return ResponseEntity.ok(toUsername(userRepository.findAll()));
  }


  private List<String> toUsername(List<User> users) {
    return users.stream().map(User::getUsername).collect(Collectors.toList());
  }
}
