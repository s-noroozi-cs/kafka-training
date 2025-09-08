package org.kafka.security.jwt.client_side.auth;

import io.jsonwebtoken.Claims;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.kafka.security.jwt.issuer.JwtTokenGenerator;

import java.util.*;

public class CustomOAuthBearerToken implements OAuthBearerToken {

  private final String tokenValue;
  private final Claims claims;

  public CustomOAuthBearerToken(String tokenValue) {
    this.tokenValue = tokenValue;
    this.claims = JwtTokenGenerator.validateToken(tokenValue);
  }

  @Override
  public String value() {
    return tokenValue;
  }

  @Override
  public Long startTimeMs() {
    return claims.getIssuedAt().getTime();
  }

  @Override
  public long lifetimeMs() {
    return claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
  }

  @Override
  public String principalName() {
    return claims.getSubject();
  }

  @Override
  public Set<String> scope() {
    // Extract roles from claims
    Object roles = claims.get("roles");
    if (roles instanceof List) {
      return new HashSet<>((Collection<String>) roles);
    }
    return Collections.emptySet();
  }
}
