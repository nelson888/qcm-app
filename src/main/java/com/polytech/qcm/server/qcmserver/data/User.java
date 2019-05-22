package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user")
@Entity
public class User implements UserDetails {

  private static final String ALPHABETIC_REGEX = "[a-zA-Z]+";

  @Id
  @NonNull
  @NotBlank
  @Pattern(regexp = ALPHABETIC_REGEX)
  private String username;
  @NonNull
  @NotBlank
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //don't deserialize it
  private String password; //should be already hashed in database

  @NonNull
  @NotBlank
  private String role; //CAREFUL ROLES MUST START WITH THE PREFIX 'ROLE_' BUT NOT IN SECURITY CONFIGURATION

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(role));
  }

  public String getRole() {
    return role;
  }
}
