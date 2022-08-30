package com.caston.wechat.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum WeChatEnum {
    APPID(),APPSECRET(),
    TEMPLATEID(),
    CITY_URL("https://geoapi.qweather.com/v2/city/lookup?key=5d3cb817dd424f98a1f92c0173283102&&location=REGION"),
    WEATHER_URL("https://devapi.qweather.com/v7/weather/TYPE?location=CITYID&key=5d3cb817dd424f98a1f92c0173283102"),
    TEXT_URL("https://devapi.qweather.com/v7/indices/1d?location=CITYID&key=5d3cb817dd424f98a1f92c0173283102&type=0"),
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
