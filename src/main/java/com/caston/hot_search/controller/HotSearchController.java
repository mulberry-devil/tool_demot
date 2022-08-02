package com.caston.hot_search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RestTemplate restTemplate;

    @GetMapping("/")
    public Map<String, JSONArray> hotSearch() {
        HashMap<String, JSONArray> map = new HashMap<>();
        JSONArray weibo = get("https://tenapi.cn/resou/");
        JSONArray zhihu = get("https://tenapi.cn/zhihuresou/");
        JSONArray douyin = get("https://tenapi.cn/douyinresou/");
        JSONArray blibli = get("https://tenapi.cn/bilihot/");
        JSONArray baidu = get("https://tenapi.cn/baiduhot/");
        map.put("微博",weibo);
        map.put("知乎",zhihu);
        map.put("抖音",douyin);
        map.put("B站",blibli);
        map.put("百度",baidu);
        return map;
    }

    public JSONArray get(String url){
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class).getBody();
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        return  jsonArray;
    }
}
