// SecurityConfig.java
package com.clinica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {

  // CORS global (carregado pelo http.cors(Customizer.withDefaults()))
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of("http://localhost:5173")); // front (Vite)
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key"));
    cfg.setExposedHeaders(List.of("Location")); // se você usa Location no header
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  // Endpoints públicos (swagger, auth, etc.)
  @Bean @Order(0)
  SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**")
      .cors(Customizer.withDefaults())         // <- AQUI
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  // API protegida por JWT
  @Bean @Order(1)
  SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher("/api/**")
      .cors(Customizer.withDefaults())         // <- AQUI TAMBÉM
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }
  @Bean
JwtDecoder jwtDecoder(@Value("${clinica.jwt.secret}") String secret) {
  byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
  SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
  return NimbusJwtDecoder.withSecretKey(key)
      .macAlgorithm(MacAlgorithm.HS256)
      .build();
}
}
