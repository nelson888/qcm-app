package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class ApplicationConfiguration {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostConstruct
  public void initDatabase() {
    String student = Role.STUDENT.roleName();
    String teacher = Role.TEACHER.roleName();

    Arrays.asList(
      new User("nelson", passwordEncoder.encode("nelson"), student),
      new User("nicolas", passwordEncoder.encode("nicolas"), student),
      new User("teacher", passwordEncoder.encode("teacher"), teacher))
      .forEach(userRepository::save);
  }

}
