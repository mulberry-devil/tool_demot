package com.caston.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum WeChatEnum {
    APPID(),APPSECRET(),
    TEMPLATEID(),
    WEATHER_URL("https://tenapi.cn/wether/?city=USERCITY"),
    ACCESS_TOKEN_URL("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET"),
    SEND_URL("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN");
    private String aliField;
    public String getAliField(){
        return aliField;
    }
    public void setAliField(String aliField){
        this.aliField = aliField;
    }
}
