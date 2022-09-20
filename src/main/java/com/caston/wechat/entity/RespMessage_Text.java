package com.caston.wechat.entity;

import lombok.Data;

@Data
public class RespMessage_Text extends RespMessage_Base{
    // 回复的消息内容
    private String Content;
}
