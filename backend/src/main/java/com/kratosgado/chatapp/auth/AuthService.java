
package com.kratosgado.chatapp.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kratosgado.chatapp.auth.dto.RegisterDto;
import com.kratosgado.chatapp.services.JwtService;
import com.kratosgado.chatapp.users.User;
import com.kratosgado.chatapp.users.UserRepo;
import com.kratosgado.chatapp.utils.UtilConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public void register(RegisterDto dto, HttpServletResponse res) {
    if (userRepo.existsByEmail(dto.email())) {
      throw new RuntimeException("Email already registered");
    }

    if (!dto.password().equals(dto.confirmPassword())) {
      throw new RuntimeException("Passwords do not match");
    }

    User user = new User();
    user.setName(dto.name());
    user.setEmail(dto.email());
    user.setPassword(passwordEncoder.encode(dto.password()));
    userRepo.save(user);

    String token = jwtService.generateToken(user);
    Cookie cookie = new Cookie("jwt", token);

    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(UtilConstants.ONE_DAY_IN_SECONDS);
    res.addCookie(cookie);
  }

}
