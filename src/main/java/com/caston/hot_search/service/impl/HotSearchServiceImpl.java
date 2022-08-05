package com.caston.hot_search.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caston.hot_search.service.HotSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * 初始化热点数据以及更新数据
 * </p>
 *
 * @author caston
 * @since 2022-08-03
 */
@Service
public class HotSearchServiceImpl implements HotSearchService {
    private static final Logger log = LoggerFactory.getLogger(HotSearchServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addHotSearch(String key, String url) {
        try {
            JSONArray hotSearch = get(url);
            redisTemplate.opsForValue().setIfPresent(key, hotSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addHotSearchInit(String key, String url) {
        try {
            log.info("开始初始化{}的数据...", key);
            JSONArray hotSearch = get(url);
            redisTemplate.opsForValue().set(key, hotSearch);
            log.info("{}数据初始化成功...", key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化{}数据异常：{}", key, e);
        }
    }

    /**
     * 对url发送请求并获得数据
     *
     * @param url
     * @return
     */
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
