package com.clinica.web;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Value("${clinica.jwt.secret}")
  private String secret;

  public record AuthReq(String username) {}
  public record AuthRes(String token) {}

  @PostMapping("/token")
  public ResponseEntity<AuthRes> token(@RequestBody AuthReq req) throws JOSEException {
    String user = (req != null && req.username() != null && !req.username().isBlank())
        ? req.username() : "user";

    Instant now = Instant.now();

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(user)
        .claim("scope", "api")
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusSeconds(60 * 60 * 8))) // 8h
        .build();

    SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    jwt.sign(new MACSigner(secret.getBytes()));

    return ResponseEntity.ok(new AuthRes(jwt.serialize()));
  }
}
