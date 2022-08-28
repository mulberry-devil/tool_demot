package com.caston.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caston.wechat.entity.*;
import com.caston.wechat.enums.WeChatEnum;
import com.caston.wechat.mapper.WechatMapper;
import com.caston.wechat.mapper.WechatNoteMapper;
import com.caston.wechat.mapper.WechatTokenMapper;
import com.caston.wechat.service.WechatNoteService;
import com.caston.wechat.service.WechatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
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

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WechatNoteMapper wechatNoteMapper;
    @Autowired
    private WechatTokenMapper wechatTokenMapper;

    @Override
    public Map<String, Content> getWeather(WechatUser wechatUser) {
        Boolean isOk = true;
        Map<String, Content> sendMag = new HashMap<>();
        while (isOk) {
            try {
                String weather_url = WeChatEnum.WEATHER_URL.getAliField().replace("USERCITY", wechatUser.getCity());
                HttpHeaders headers = new HttpHeaders();
                headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
                HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
                String response = restTemplate.exchange(weather_url, HttpMethod.GET, entity, String.class).getBody();
                JSONObject json = JSONObject.parseObject(response);
                JSONObject data = json.getJSONObject("data").getJSONArray("forecast").getJSONObject(0);
                String high = data.getString("high").split(" ")[1];
                String low = data.getString("low").split(" ")[1];
                String fengxiang = data.getString("fengxiang");
                String type = data.getString("type");
                String wendu = json.getJSONObject("data").getString("wendu");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                sendMag.put("date", new Content(formatter.format(new Date()), "#f6bec8"));
                sendMag.put("city", new Content(wechatUser.getCity()));
                sendMag.put("wether", new Content(type));
                sendMag.put("current", new Content(wendu));
                sendMag.put("high", new Content(high));
                sendMag.put("low", new Content(low));
                sendMag.put("fengxiang", new Content(fengxiang));
                long now = formatter.parse(formatter.format(new Date()).split(" ")[0]).getTime();
                long birthday = wechatUser.getBirthday().getTime();
                String day = String.valueOf((now - birthday) / 24 / 60 / 60 / 1000);
                sendMag.put("day", new Content(day, "#eeb8c3"));
                WechatNote wechatNote = wechatNoteMapper.selectOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getUserid, wechatUser.getUserId()).eq(WechatNote::getIsnew, 1));
                sendMag.put("note", new Content(wechatNote == null ? "无" : wechatNote.getNote().replace(";", "\n"), "#f8df72"));
                isOk = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sendMag;
    }

    @Override
    public String getAccessToken(WechatUser wechatUser) {
        WechatToken wechatToken = wechatTokenMapper.selectOne(null);
        String access_token = "";
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
        return access_token;
    }

    @Override
    public String send(WechatUser wechatUser, String accessToken, Map<String, Content> weather) {
        String url = WeChatEnum.SEND_URL.getAliField().replace("ACCESS_TOKEN", accessToken);
        Map<String, Object> sendBody = new HashMap<>();
        sendBody.put("touser", wechatUser.getUserId());
        sendBody.put("topcolor", "#FF0000");
        sendBody.put("data", weather);
        sendBody.put("template_id", WeChatEnum.TEMPLATEID.getAliField());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, sendBody, String.class);
        JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
        System.out.println(jsonObject);
        wechatNoteMapper.update(null, new LambdaUpdateWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, wechatUser.getUserId()).set(WechatNote::getIsnew, 0));
        return "success";
    }

    public WechatToken getToken() {
        String url = WeChatEnum.ACCESS_TOKEN_URL.getAliField().replace("APPID", WeChatEnum.APPID.getAliField()).replace("APPSECRET", WeChatEnum.APPSECRET.getAliField());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(forEntity.getBody());
        Object errcode = jsonObject.get("errcode");
        if (errcode != null && "40013".equals(errcode.toString())) {
            return null;
        }
        String access_token = jsonObject.getString("access_token");
        int expires_in = jsonObject.getIntValue("expires_in");
        WechatToken token = new WechatToken(access_token, new Date(), expires_in);
        return token;
    }
}
