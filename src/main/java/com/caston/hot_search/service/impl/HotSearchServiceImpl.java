package com.caston.hot_search.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caston.hot_search.service.HotSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HotSearchServiceImpl implements HotSearchService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addHotSearch(String key, String url) {
        JSONArray hotSearch = get(url);
        redisTemplate.opsForValue().setIfPresent(key, hotSearch);
    }

    @Override
    public void addHotSearchInit(String key, String url) {
        JSONArray hotSearch = get(url);
        redisTemplate.opsForValue().set(key, hotSearch);
    }

    public JSONArray get(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        return jsonArray;
    }
}
