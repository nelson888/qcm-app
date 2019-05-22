package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController("/reponse")
public class ResponseController {
  private final ChoiceRepository choiceRepository;
  private final UserRepository userRepository;
  private final ResponseRepository responseRepository;

  public ResponseController(ChoiceRepository choiceRepository,
                            UserRepository userRepository,
                            ResponseRepository responseRepository) {
    this.choiceRepository = choiceRepository;
    this.userRepository = userRepository;
    this.responseRepository = responseRepository;
  }
/*
  @GetMapping(value = "/username")
  @ResponseBody
  public String currentUserName(Principal user) {
    return principal.getName();
  }*/

  @PostMapping("/")
  @ResponseBody
  public ResponseEntity postResponse(Principal user, @RequestBody Choice c) {
    Response response = new Response();
    Choice choice = choiceRepository.findById(c.getId()).orElseThrow(() -> new BadRequestException("Choice with id " + c.getId() + " doesn't exists"));
    response.setUser(userRepository.findByUsername(user.getName()).get());
    response.setChoice(choice);
    responseRepository.saveAndFlush(response);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
