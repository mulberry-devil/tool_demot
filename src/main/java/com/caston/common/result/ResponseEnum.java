package com.caston.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum {

    SUCCESS(200,"响应成功"),
    ERROR(-200,"响应失败");

    private Integer code;
    private String message;
}
