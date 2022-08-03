package com.caston.init;

import com.caston.hot_search.service.HotSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DemoInit {

    @Autowired
    private HotSearchService hotSearchService;

    /*
    {
    "weibo":"https://tenapi.cn/resou/",
    "zhihu":"https://tenapi.cn/zhihuresou/",
    "douyin":"https://tenapi.cn/douyinresou/",
    "blibli":"https://tenapi.cn/bilihot/",
    "baidu":"https://tenapi.cn/baiduhot/"
}
     */

    @PostConstruct
    public void init() {
        System.out.println("初始化redis中数据");
        try {
            hotSearchService.addHotSearchInit("weibo", "https://tenapi.cn/resou/");
            hotSearchService.addHotSearchInit("zhihu", "https://tenapi.cn/zhihuresou/");
            hotSearchService.addHotSearchInit("douyin", "https://tenapi.cn/douyinresou/");
            hotSearchService.addHotSearchInit("blibli", "https://tenapi.cn/bilihot/");
            hotSearchService.addHotSearchInit("baidu", "https://tenapi.cn/baiduhot/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
