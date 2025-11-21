package com.kratosgado.chatapp.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kratosgado.chatapp.auth.dto.RegisterDto;
import com.kratosgado.chatapp.users.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;

  @PostMapping("/login")
  public String login() {
    logger.info("logging in user");
    return "login";
  }

  @PostMapping("/register")
  public User register(@RequestBody @Valid RegisterDto dto, HttpServletResponse res) {
    logger.info("registering user {}", dto);
    return authService.register(dto, res);
  }
}
