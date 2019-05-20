package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.repository.FakeUserDetailsRepository;
import com.polytech.qcm.server.qcmserver.repository.UserDetailsRepository;
import com.polytech.qcm.server.qcmserver.security.JwtConfigurer;
import com.polytech.qcm.server.qcmserver.security.JwtTokenProvider;
import com.polytech.qcm.server.qcmserver.security.Role;
import com.polytech.qcm.server.qcmserver.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.jwt.token.secret-key:secret}")
  private String secretKey = "secret";

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public String secretKey() {
    return Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public List<UserDetailsImpl> users(PasswordEncoder passwordEncoder) {
    return Arrays.asList(
      new UserDetailsImpl("nelson", passwordEncoder.encode("nelson"), Role.STUDENT),
      new UserDetailsImpl("nicolas", passwordEncoder.encode("nicolas"), Role.STUDENT),
      new UserDetailsImpl("teacher", passwordEncoder.encode("teacher"), Role.TEACHER));
  }

  @Bean
  public UserDetailsRepository userRepository(List<UserDetailsImpl> users) {
    return new FakeUserDetailsRepository(users);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final String student = Role.STUDENT.name();
    final String teacher = Role.TEACHER.name();
    final String admin = Role.ADMIN.name();
    http
      .httpBasic().disable()
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/auth/login").permitAll()
      //TODO fill when doing controllers
      //CommandController
      .antMatchers(HttpMethod.POST, "/api/commands/").hasAnyRole(student, admin)
      .antMatchers(HttpMethod.GET, "/api/commands/consume").hasAnyRole(admin, teacher)
      //ExecutionController
      .antMatchers(HttpMethod.POST, "/api/executions/").hasAnyRole(student, admin)
      .antMatchers(HttpMethod.GET, "/api/executions/").authenticated()
      .antMatchers(HttpMethod.DELETE, "/api/executions/**").hasAnyRole(student, admin)
      .antMatchers(HttpMethod.PUT, "/api/executions/**").hasAnyRole(student, admin)
      .antMatchers(HttpMethod.GET, "/api/executions/count").authenticated()
      .antMatchers(HttpMethod.GET, "/api/executions/soonest").authenticated()
      .antMatchers(HttpMethod.GET, "/api/executions/current").authenticated()
      .antMatchers(HttpMethod.GET, "/api/executions/").authenticated()
      //StateController
      .antMatchers(HttpMethod.GET, "/api/state").authenticated()
      .antMatchers(HttpMethod.PUT, "/api/state").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.GET, "/api/globalState").hasRole(student)
      //StorageController
      .antMatchers(HttpMethod.PUT, "/storage/**").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.GET, "/storage/**").hasAnyRole(student, admin)

      .anyRequest().authenticated()
      .and()
      .apply(new JwtConfigurer(jwtTokenProvider));
  }

}
