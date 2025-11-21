package com.kratosgado.chatapp.utils;

import org.springframework.http.HttpStatus;

public class ApiResponse {
  private String message;
  private Object data;
  private HttpStatus status;

  public ApiResponse(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public ApiResponse(HttpStatus status, String message, Object data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public static ApiResponse ok(String message) {
    return new ApiResponse(HttpStatus.OK, message);
  }

  public static ApiResponse okWithData(String message, Object data) {
    return new ApiResponse(HttpStatus.OK, message, data);
  }

  public static ApiResponse created(String message) {
    return new ApiResponse(HttpStatus.CREATED, message);
  }

  public static ApiResponse createdWithData(String message, Object data) {
    return new ApiResponse(HttpStatus.CREATED, message, data);
  }

}
