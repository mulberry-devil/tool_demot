package com.caston.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum WeChatEnum {
    APPID(),APPSECRET(),
    TEMPLATEID(),
    CITY_URL("https://geoapi.qweather.com/v2/city/lookup?key=MYKEY&&location=REGION"),
    WEATHER_URL("https://devapi.qweather.com/v7/weather/TYPE?location=CITYID&key=MYKEY"),
    TEXT_URL("https://devapi.qweather.com/v7/indices/1d?location=CITYID&key=MYKEY&type=3,5,6,13,16"),
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
