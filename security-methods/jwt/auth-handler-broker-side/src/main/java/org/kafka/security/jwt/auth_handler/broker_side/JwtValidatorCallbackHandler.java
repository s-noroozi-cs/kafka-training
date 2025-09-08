package org.kafka.security.jwt.auth_handler.broker_side;

import io.jsonwebtoken.Claims;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.kafka.security.jwt.issuer.JwtTokenGenerator;

import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtValidatorCallbackHandler implements AuthenticateCallbackHandler {
  @Override
  public void configure(
      Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries) {
    // Configuration if needed
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (callback instanceof OAuthBearerValidatorCallback) {
        OAuthBearerValidatorCallback validationCallback = (OAuthBearerValidatorCallback) callback;
        try {
          // Validate JWT token
          Claims claims = JwtTokenGenerator.validateToken(validationCallback.tokenValue());

          // Set principal and scope
          validationCallback.token().principalName(); // Username
          validationCallback.token().scope(); // Roles/permissions

          validationCallback.token().startTimeMs(); // Token issue time
          validationCallback.token().lifetimeMs(); // Token expiration

        } catch (Exception e) {
          validationCallback.error("invalid_token", "Token validation failed: ", e.getMessage());
        }
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  @Override
  public void close() {
    // Cleanup resources
  }
}
