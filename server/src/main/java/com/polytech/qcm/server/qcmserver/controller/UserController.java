package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Api(value = "Controller to view list of users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/teachers")
  @ApiOperation(value = "Get list of all teachers", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully get the list"),
    @ApiResponse(code = 403, message = "You are not a teacher"),
  })
  public ResponseEntity getTeachers() {
    return ResponseEntity.ok(toUsername(userRepository.findAllByRole(Role.TEACHER)));
  }

  @GetMapping("/students")
  @ApiOperation(value = "Get list of all students", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully get the list"),
    @ApiResponse(code = 403, message = "You are not a teacher"),
  })
  public ResponseEntity getStudents(){
    return ResponseEntity.ok(toUsername(userRepository.findAllByRole(Role.STUDENT)));
  }

  @GetMapping("/all")
  @ApiOperation(value = "Get list of all users", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully get the list"),
    @ApiResponse(code = 403, message = "You are not a teacher"),
  })
  public ResponseEntity getEveryone(){
    return ResponseEntity.ok(toUsername(userRepository.findAll()));
  }


  private List<String> toUsername(List<User> users) {
    return users.stream().map(User::getUsername).collect(Collectors.toList());
  }
}
