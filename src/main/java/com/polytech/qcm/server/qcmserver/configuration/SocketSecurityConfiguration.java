package com.polytech.qcm.server.qcmserver.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(
    MessageSecurityMetadataSourceRegistry messages) {
    messages
      .simpDestMatchers("/secured/**").authenticated()
      .anyMessage().authenticated();
  }

}
