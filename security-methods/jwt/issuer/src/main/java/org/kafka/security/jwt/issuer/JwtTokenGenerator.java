package org.kafka.security.jwt.issuer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

public class JwtTokenGenerator {
  private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

  private static final long EXPIRATION_TIME = 3600000; // 1 hour

  public static String generateToken(String username, String[] roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);
    claims.put("username", username);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SECRET_KEY)
        .compact();
  }

  public static Claims validateToken(String token) {
    try {
      return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    } catch (Exception e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  public static Key getSecretKey() {
    return SECRET_KEY;
  }
}
