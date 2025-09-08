package org.kafka.security.jwt.client_side.auth;

import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CustomLoginCallbackHandler implements AuthenticateCallbackHandler {

  private String jwtToken;

  @Override
  public void configure(
          Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries) {
    // Extract token from JAAS configuration
    String jaasConfig = (String) configs.get("sasl.jaas.config");
    if (jaasConfig != null) {
      int tokenStart = jaasConfig.indexOf("oauth.token=\"") + "oauth.token=\"".length();
      int tokenEnd = jaasConfig.indexOf("\"", tokenStart);
      jwtToken = jaasConfig.substring(tokenStart, tokenEnd);
    }
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (callback instanceof OAuthBearerTokenCallback) {
        OAuthBearerTokenCallback tokenCallback = (OAuthBearerTokenCallback) callback;
        if (jwtToken != null) {
          tokenCallback.token(new CustomOAuthBearerToken(jwtToken));
        } else {
          tokenCallback.error("missing_token", "JWT token not provided", null);
        }
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  @Override
  public void close() {
    // Cleanup
  }
}
