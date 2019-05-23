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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.jwt.token.secret-key}")
  private String secretKey; //encrypting base key for jwt token generation

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
      }
    };
  }

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

  //swagger at url http://localhost:8080/swagger-ui.html
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
      //AuthController
      .antMatchers("/auth/login").permitAll()
      .antMatchers(HttpMethod.GET, "/auth/session/**").permitAll()
      //QcmController
      .antMatchers(HttpMethod.GET, "/qcm/new").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.GET, "/qcm/**/nextQuestion").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.PUT, "/qcm/**").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.PUT, "/qcm/**").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.POST, "/qcm/**").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.DELETE, "/qcm/**").hasAnyRole(teacher, admin)
      .antMatchers(HttpMethod.GET, "/qcm/**").authenticated()
      //UserController
      .antMatchers("/user/**").hasAnyRole(teacher, admin)
      //QuestionController
      .antMatchers("/question/**").authenticated()
      //ResponseController
      .antMatchers(HttpMethod.POST, "/response/**").hasAnyRole(student, admin)
      //other requests
      .anyRequest().permitAll()
      .and()
      .apply(new JwtConfigurer(jwtTokenProvider));
  }

}
