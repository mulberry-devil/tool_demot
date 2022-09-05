package com.caston.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caston.init.DemoInit;
import com.caston.wechat.controller.WechatController;
import com.caston.wechat.entity.*;
import com.caston.wechat.enums.WeChatEnum;
import com.caston.wechat.exception.WeChatException;
import com.caston.wechat.mapper.WechatMapper;
import com.caston.wechat.mapper.WechatNoteMapper;
import com.caston.wechat.mapper.WechatTemplateMapper;
import com.caston.wechat.mapper.WechatTokenMapper;
import com.caston.wechat.service.WechatNoteService;
import com.caston.wechat.service.WechatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-08-27
 */
@Service
public class WechatServiceImpl extends ServiceImpl<WechatMapper, Wechat> implements WechatService {

    private static final Logger log = LoggerFactory.getLogger(WechatServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WechatNoteMapper wechatNoteMapper;
    @Autowired
    private WechatTokenMapper wechatTokenMapper;

    @Override
    @Transactional
    public Map<String, Object> getWeather(WechatUser wechatUser) {
        Boolean isOk = true;
        Map<String, Object> msg = new HashMap<>();
        Map<String, Content> wether_sendMag = null;
        Map<String, Content> wether_note_sendMag = new HashMap<>();
        Map<String, Content> note_sendMag = null;
        int errorNum = 5;
        while (isOk) {
            try {
                log.info("开始获取{}的天气数据...", wechatUser.getCity());
                String city_url = WeChatEnum.CITY_URL.getAliField().replace("REGION", wechatUser.getRegion());
                ResponseEntity<String> city = restTemplate.getForEntity(city_url, String.class);
                JSONObject city_json = JSONObject.parseObject(city.getBody());
                JSONArray location = city_json.getJSONArray("location");
                for (Object i : location) {
                    JSONObject region_json = (JSONObject) i;
                    String adm2 = region_json.getString("adm2");
                    if (adm2.contains(wechatUser.getCity())) {
                        String id = region_json.getString("id");
                        String weather_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "3d").replace("CITYID", id);
                        ResponseEntity<String> weather = restTemplate.getForEntity(weather_url, String.class);
                        JSONObject weather_json = JSONObject.parseObject(weather.getBody());
                        String weather_now_url = WeChatEnum.WEATHER_URL.getAliField().replace("TYPE", "now").replace("CITYID", id);
                        ResponseEntity<String> weather_now = restTemplate.getForEntity(weather_now_url, String.class);
                        JSONObject weather_now_json = JSONObject.parseObject(weather_now.getBody());
                        String text_url = WeChatEnum.TEXT_URL.getAliField().replace("CITYID", id);
                        ResponseEntity<String> text = restTemplate.getForEntity(text_url, String.class);
                        JSONObject text_json = JSONObject.parseObject(text.getBody());
                        JSONObject daily = weather_json.getJSONArray("daily").getJSONObject(0);
                        String fxDate = daily.getString("fxDate");
                        String textDay = daily.getString("textDay");
                        String textNight = daily.getString("textNight");
                        String tempMax = daily.getString("tempMax");
                        String tempMin = daily.getString("tempMin");
                        String windDirDay = daily.getString("windDirDay");
                        String windScaleDay = daily.getString("windScaleDay");
                        String windSpeedDay = daily.getString("windSpeedDay");
                        String windDirNight = daily.getString("windDirNight");
                        String windScaleNight = daily.getString("windScaleNight");
                        String windSpeedNight = daily.getString("windSpeedNight");
                        String uvIndex = daily.getString("uvIndex");
                        String vis = daily.getString("vis");
                        String temp = weather_now_json.getJSONObject("now").getString("temp");
                        StringBuilder stringBuilder = new StringBuilder();
                        JSONArray text_total = text_json.getJSONArray("daily");
                        int k = 1;
                        for (Object j : text_total) {
                            JSONObject json = (JSONObject) j;
                            stringBuilder.append(k++ + ". " + json.getString("text") + "\n");
                        }
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        long now = 0;
                        now = formatter.parse(formatter.format(new Date()).split(" ")[0]).getTime();
                        long birthday = wechatUser.getBirthday().getTime();
                        String day = String.valueOf((now - birthday) / 24 / 60 / 60 / 1000);
                        WechatNote wechatNote = wechatNoteMapper.selectOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getUserid, wechatUser.getUserId()).eq(WechatNote::getIsnew, 1));
                        log.info("获取{}的天气数据完成，开始封装模板数据...", wechatUser.getRegion());
                        MessageMap.Builder weather_builder = new MessageMap.Builder();
                        wether_sendMag = weather_builder.put("fxDate", fxDate, "#f6bec8").put("region", wechatUser.getRegion())
                                .put("textDay", textDay).put("textNight", textNight)
                                .put("temp", temp).put("tempMax", tempMax)
                                .put("tempMin", tempMin).put("windDirDay", windDirDay)
                                .put("windScaleDay", windScaleDay).put("windSpeedDay", windSpeedDay)
                                .put("windDirNight", windDirNight).put("windScaleNight", windScaleNight)
                                .put("windSpeedNight", windSpeedNight).put("uvIndex", uvIndex).put("vis", vis)
                                .put("day", day, "#eeb8c3")
                                .put("note", wechatNote == null ? "无" : wechatNote.getNote().replace(";", "\n").replace("；", "\n"), "#f8df72").build();
                        wether_note_sendMag.put("text", new Content(stringBuilder.toString()));
                        if (wechatNote != null) {
                            note_sendMag = new HashMap<>();
                            note_sendMag.put("note", new Content(wechatNote.getNote().replace(";", "\n").replace("；", "\n"), "#f8df72"));
                        }
                        log.info("封装模板数据完成...");
                    }
                }
                msg.put("1", wether_sendMag);
                msg.put("2", wether_note_sendMag);
                if (note_sendMag != null) {
                    msg.put("3", note_sendMag);
                }
                isOk = false;
            } catch (Exception e) {
                log.error("获取{}的天气数据失败，开始重新获取：", wechatUser.getCity(), e);
                errorNum--;
                if (errorNum == 0) {
                    isOk = false;
                }
            }
        }
        return msg;
    }

    @Override
    @Transactional
    public String getAccessToken(WechatUser wechatUser) {
        log.info("开始获取有效的access_token...");
        WechatToken wechatToken = wechatTokenMapper.selectOne(null);
        String access_token = "";
        try {
            if (wechatToken == null) {
                WechatToken token = getToken();
                access_token = token.getAccessToken();
                wechatTokenMapper.insert(token);
            } else {
                Date now = new Date();
                Date expire = new Date(wechatToken.getStartTime().getTime() + wechatToken.getExpiresIn() * 1000);
                if (now.compareTo(expire) != -1) {
                    WechatToken token = getToken();
                    access_token = token.getAccessToken();
                    wechatTokenMapper.update(token, null);
                } else {
                    access_token = wechatToken.getAccessToken();
                }
            }
            log.info("获取有效的access_token完成...");
        } catch (Exception e) {
            log.error("获取access_token失败：", e);
        }
        return access_token;
    }

    @Override
    @Transactional
    public void send(WechatUser wechatUser, String accessToken, Map<String, Object> msg) {
        try {
            log.info("开始推送微信模板给用户...");
            String url = WeChatEnum.SEND_URL.getAliField().replace("ACCESS_TOKEN", accessToken);
            Map<String, Object> sendBody = new HashMap<>();
            sendBody.put("touser", wechatUser.getUserId());
            sendBody.put("topcolor", "#FF0000");
            // 天气情况 1
            WeChatEnum.TEMPLATEID.setAliField(DemoInit.TEMPLATEMAP.get(1));
            sendBody.put("data", msg.get("1"));
            sendBody.put("template_id", WeChatEnum.TEMPLATEID.getAliField());
            ResponseEntity<String> forEntity = restTemplate.postForEntity(url, sendBody, String.class);
            JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
            log.info("推送结果：{}", jsonObject);
            // 天气温馨提醒 2
            WeChatEnum.TEMPLATEID.setAliField(DemoInit.TEMPLATEMAP.get(2));
            sendBody.replace("data", msg.get("2"));
            sendBody.replace("template_id", WeChatEnum.TEMPLATEID.getAliField());
            forEntity = restTemplate.postForEntity(url, sendBody, String.class);
            jsonObject = JSONObject.parseObject(forEntity.getBody());
            log.info("推送结果：{}", jsonObject);
            // 重要提醒 3
            if (msg.size() > 2) {
                WeChatEnum.TEMPLATEID.setAliField(DemoInit.TEMPLATEMAP.get(3));
                sendBody.put("data", msg.get("3"));
                sendBody.put("template_id", WeChatEnum.TEMPLATEID.getAliField());
                forEntity = restTemplate.postForEntity(url, sendBody, String.class);
                jsonObject = JSONObject.parseObject(forEntity.getBody());
                log.info("推送结果：{}", jsonObject);
                wechatNoteMapper.update(null, new LambdaUpdateWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, wechatUser.getUserId()).set(WechatNote::getIsnew, 0));
            }
        } catch (Exception e) {
            log.error("推送微信模板给用户失败：", e);
        }
    }

    public WechatToken getToken() {
        Boolean isOk = true;
        WechatToken token = null;
        int errorNum = 5;
        while (isOk) {
            try {
                log.info("开始从微信官方获取access_token...");
                String url = WeChatEnum.ACCESS_TOKEN_URL.getAliField().replace("APPID", WeChatEnum.APPID.getAliField()).replace("APPSECRET", WeChatEnum.APPSECRET.getAliField());
                ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
                JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
                Object errcode = jsonObject.get("errcode");
                if (errcode != null) {
                    throw new WeChatException("获取失败");
                }
                String access_token = jsonObject.getString("access_token");
                int expires_in = jsonObject.getIntValue("expires_in");
                token = new WechatToken(access_token, new Date(), expires_in);
                log.info("从微信官方获取access_token成功...");
                isOk = false;
            } catch (Exception e) {
                log.error("从微信官方获取access_token失败：", e);
                errorNum--;
                if (errorNum == 0) {
                    isOk = false;
                }
            }
        }
        return token;
    }
}
