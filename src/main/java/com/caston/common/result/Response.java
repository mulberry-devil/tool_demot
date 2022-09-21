package com.caston.common.result;

import lombok.Data;

import java.io.Serializable;

import static com.caston.common.result.ResponseEnum.*;

@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;

    private Response() {
    }

    public static <T> Response<T> success() {
        return success(SUCCESS);
    }

    public static <T> Response<T> success(T data) {
        return success(SUCCESS, data);
    }

    public static <T> Response<T> success(ResponseEnum responseEnum) {
        return success(responseEnum, null);
    }

    public static <T> Response<T> success(ResponseEnum responseEnum, T data) {
        Integer code = responseEnum.getCode();
        String message = responseEnum.getMessage();
        return success(code, message, data);
    }

    public static <T> Response<T> success(Integer code, String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error() {
        return error(ERROR);
    }

    public static <T> Response<T> error(T data) {
        return error(ERROR, data);
    }

    public static <T> Response<T> error(ResponseEnum responseEnum) {
        return error(responseEnum, null);
    }

    public static <T> Response<T> error(ResponseEnum responseEnum, T data) {
        Integer code = responseEnum.getCode();
        String message = responseEnum.getMessage();
        return error(code, message, data);
    }

    public static <T> Response<T> error(Integer code, String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public Response<T> data(T data) {
        this.setData(data);
        return this;
    }

    public Response<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    public Response<T> code(Integer code) {
        this.setCode(code);
        return this;
    }
}
