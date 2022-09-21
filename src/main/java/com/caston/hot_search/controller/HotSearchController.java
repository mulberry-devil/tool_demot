package com.caston.hot_search.controller;

import com.caston.common.result.Response;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hotSearch")
@RequiresRoles(value = {"manager", "user"}, logical = Logical.OR)
public class HotSearchController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/")
    public Response hotSearch() {
        HashMap<String, Object> map = new HashMap<>();
        Object weibo = redisTemplate.opsForValue().get("weibo");
        Object zhihu = redisTemplate.opsForValue().get("zhihu");
        Object douyin = redisTemplate.opsForValue().get("douyin");
        Object blibli = redisTemplate.opsForValue().get("blibli");
        Object baidu = redisTemplate.opsForValue().get("baidu");
        map.put("微博", weibo);
        map.put("知乎", zhihu);
        map.put("抖音", douyin);
        map.put("B站", blibli);
        map.put("百度", baidu);
        return Response.success(map);
    }
}
