package com.kratosgado.chatapp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/login")
  public String login() {
    logger.info("logging in user");
    return "login";
  }

  @PostMapping("/register")
  public String register() {
    logger.info("logging in user");
    return "register";
  }
}
