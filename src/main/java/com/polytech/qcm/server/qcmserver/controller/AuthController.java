package com.polytech.qcm.server.qcmserver.controller;

import com.polytech.qcm.server.qcmserver.data.response.AuthResponse;
import com.polytech.qcm.server.qcmserver.exception.BadCredentialsException;
import com.polytech.qcm.server.qcmserver.exception.NotFoundException;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import com.polytech.qcm.server.qcmserver.security.JwtTokenProvider;
import com.polytech.qcm.server.qcmserver.data.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Api(value = "Controller to authenticate")
public class AuthController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider,
                        UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  @ApiOperation(value = "Returns Json Web Token for the user to authenticates in his following requests", response = AuthResponse.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "The JWT"),
    @ApiResponse(code = 400, message = "The credentials supplied aren't correct"),
  })
  public ResponseEntity login(@RequestBody User data) { // only needs username and password (not hashed)
    try {
      String username = data.getUsername();
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
      User user = this.userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Username " + username + "not found"));
      String token = jwtTokenProvider.createToken(username, user.getRole());
      LOGGER.info("User {} authenticated successfully", username);
      return ResponseEntity.ok(new AuthResponse(username, user.getRole(), token));
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username/password supplied");
    }
  }

}