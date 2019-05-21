package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.security.JwtConfigurer;
import com.polytech.qcm.server.qcmserver.security.JwtTokenProvider;
import com.polytech.qcm.server.qcmserver.data.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.jwt.token.secret-key}")
  private String secretKey; //encrypting base key for jwt token generation

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final String student = Role.STUDENT.name();
    final String teacher = Role.TEACHER.name();
    final String admin = Role.ADMIN.name();
    http
      .cors()
      .and()
      .csrf()
      .disable()
      .httpBasic().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/auth/login").permitAll()
      .antMatchers(HttpMethod.GET, "/auth/student/session").hasRole(student)
      .antMatchers(HttpMethod.GET, "/auth/teacher/session").hasRole(teacher)
      //TODO fill when doing controllers
      //QcmController
      .antMatchers(HttpMethod.GET, "/qcm/new").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.GET, "/qcm/**").authenticated()
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
