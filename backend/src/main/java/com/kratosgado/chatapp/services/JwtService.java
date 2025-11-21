package com.kratosgado.chatapp.services;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kratosgado.chatapp.users.User;
import com.kratosgado.chatapp.utils.UtilConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  public String generateToken(User user) {
    return Jwts.builder().setSubject(user.getId())
        .claim("email", user.getEmail())
        .claim("name", user.getName())
        .setExpiration(Date.from(Instant.now().plusSeconds(UtilConstants.ONE_DAY_IN_SECONDS)))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

}
