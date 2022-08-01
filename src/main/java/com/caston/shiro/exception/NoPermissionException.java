package com.caston.shiro.exception;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NoPermissionException {

    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedException(){
        return "无权限";
    }

    @ExceptionHandler(AuthorizationException.class)
    public String authorizationException(){
        return "权限认证失败";
    }
}
