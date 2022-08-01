package com.caston.shiro.service.impl;

import com.caston.shiro.entity.Account;
import com.caston.shiro.mapper.AccountMapper;
import com.caston.shiro.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-08-01
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
