package com.caston.init;

import com.caston.hot_search.service.HotSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 初始化热点数据
 * </p>
 *
 * @author caston
 * @since 2022-08-03
 */
@Component
public class DemoInit {

    private static final Logger log = LoggerFactory.getLogger(DemoInit.class);

    @Autowired
    private HotSearchService hotSearchService;

    @PostConstruct
    public void init() {
        log.info("项目启动，开始初始化热点数据......");
        try {
            hotSearchService.addHotSearchInit("weibo", "https://tenapi.cn/resou/");
            hotSearchService.addHotSearchInit("zhihu", "https://tenapi.cn/zhihuresou/");
            hotSearchService.addHotSearchInit("douyin", "https://tenapi.cn/douyinresou/");
            hotSearchService.addHotSearchInit("blibli", "https://tenapi.cn/bilihot/");
            hotSearchService.addHotSearchInit("baidu", "https://tenapi.cn/baiduhot/");
            log.info("热点数据初始化结束......");
        } catch (Exception e) {
            log.error("初始化数据发生异常：{}", e);
        }
    }
}
