package com.caston.hot_search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    private RestTemplate restTemplate;

    @GetMapping("/")
    public Map<String, JSONObject> hotSearch() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        Object response = restTemplate.exchange("https://tenapi.cn/resou/", HttpMethod.GET,entity,Object.class);

        System.out.println(response);
        // JSONObject object = restTemplate.getForObject("https://tenapi.cn/resou/", JSONObject.class);
        HashMap<String, JSONObject> map = new HashMap<>();
        // String weibo = get("https://tenapi.cn/resou/");
        // JSONObject zhihu = get("https://tenapi.cn/zhihuresou/");
        // JSONObject douyin = get("https://tenapi.cn/douyinresou/");
        // JSONObject blibli = get("https://tenapi.cn/bilihot/");
        // JSONObject baidu = get("https://tenapi.cn/baiduhot/");
        return map;
    }

}
