package com.kratosgado.chatapp.utils;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  private final HttpStatus status;

  public ApiException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public static ApiException badRequest(String message) {
    throw new ApiException(HttpStatus.BAD_REQUEST, message);
  }

  public static ApiException unauthorized(String message) {
    throw new ApiException(HttpStatus.UNAUTHORIZED, message);
  }

  public static ApiException forbidden(String message) {
    throw new ApiException(HttpStatus.FORBIDDEN, message);
  }

  public static ApiException notFound(String message) {
    throw new ApiException(HttpStatus.NOT_FOUND, message);
  }

  public static ApiException conflict(String message) {
    throw new ApiException(HttpStatus.CONFLICT, message);
  }

  public static ApiException unprocessableEntity(String message) {
    throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, message);
  }

  public static ApiException internalServerError(String message) {
    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  public static ApiException serviceUnavailable(String message) {
    throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, message);
  }

}
