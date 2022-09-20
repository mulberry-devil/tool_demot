package com.caston.wechat.service;

import com.caston.wechat.entity.Content;
import com.caston.wechat.entity.RespMessage_Text;
import com.caston.wechat.entity.Wechat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caston.wechat.entity.WechatUser;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caston
 * @since 2022-08-27
 */
public interface WechatService extends IService<Wechat> {
    public Map<String, Object> getWeather(WechatUser wechatUser);

    public String getAccessToken(WechatUser wechatUser);

    public void send(WechatUser wechatUser, String accessToken, Map<String, Object> msg);

    public String wxSignatureCheck(String signature, String timestamp, String nonce, String echostr);

    public void sendMessage2Wechat(HttpServletResponse response, RespMessage_Text responseText, String toUserName, String userId, StringBuilder builder);
}
