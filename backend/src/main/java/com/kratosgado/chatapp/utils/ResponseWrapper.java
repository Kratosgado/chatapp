
package com.kratosgado.chatapp.utils;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseWrapper implements ResponseBodyAdvice<Object> {
  private static final List<String> IGNORED_PATHS = List.of("/docs/**", "/swagger-ui.html", "/swagger-ui/**");

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr != null) {
      String path = attr.getRequest().getRequestURI();
      return !IGNORED_PATHS.contains(path);
    }

    return !returnType.getParameterType().equals(ApiResponse.class)
        && !returnType.getParameterType().equals(ResponseEntity.class);

  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {

    if (body instanceof String)
      return ApiResponse.ok((String) body);

    if (body instanceof ApiResponse)
      return body;

    return ApiResponse.okWithData("success", body);
  }

}
