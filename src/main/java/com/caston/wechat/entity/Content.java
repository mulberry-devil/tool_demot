package com.caston.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String value;
    private String color;

    public Content(String value) {
        this.value = value;
        this.color = "#173177";
    }
}
