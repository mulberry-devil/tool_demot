package com.caston.wechat.service.impl;

import com.caston.wechat.entity.WechatMessage;
import com.caston.wechat.mapper.WechatMessageMapper;
import com.caston.wechat.service.WechatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-09-20
 */
@Service
public class WechatMessageServiceImpl extends ServiceImpl<WechatMessageMapper, WechatMessage> implements WechatMessageService {

}
