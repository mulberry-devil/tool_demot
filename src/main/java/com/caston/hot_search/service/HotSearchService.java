package com.caston.hot_search.service;

/**
 * <p>
 * 初始化热点数据以及更新数据
 * </p>
 *
 * @author caston
 * @since 2022-08-03
 */
public interface HotSearchService {
    /**
     * 更新redis中热点数据
     *
     * @param key 热点数据名
     * @param url 热点数据api
     */
    public void addHotSearch(String key, String url);

    /**
     * 初始化redis中热点数据
     *
     * @param key 热点数据名
     * @param url 热点数据api
     */
    public void addHotSearchInit(String key, String url);
}
