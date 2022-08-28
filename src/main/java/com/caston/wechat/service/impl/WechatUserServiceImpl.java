package com.caston.wechat.service.impl;

import com.caston.wechat.entity.WechatUser;
import com.caston.wechat.mapper.WechatUserMapper;
import com.caston.wechat.service.WechatUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-08-27
 */
@Service
public class WechatUserServiceImpl extends ServiceImpl<WechatUserMapper, WechatUser> implements WechatUserService {

}
