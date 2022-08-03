package com.caston.hot_search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hotSearch")
@RequiresRoles(value = {"manager", "user"}, logical = Logical.OR)
public class HotSearchController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/")
    public Map<String, Object> hotSearch() {
        HashMap<String, Object> map = new HashMap<>();
        Object weibo = redisTemplate.opsForValue().get("weibo");
        Object zhihu = redisTemplate.opsForValue().get("zhihu");
        Object douyin = redisTemplate.opsForValue().get("douyin");
        Object blibli = redisTemplate.opsForValue().get("blibli");
        Object baidu = redisTemplate.opsForValue().get("baidu");
        map.put("微博",weibo);
        map.put("知乎",zhihu);
        map.put("抖音",douyin);
        map.put("B站",blibli);
        map.put("百度",baidu);
        return map;
    }
}
