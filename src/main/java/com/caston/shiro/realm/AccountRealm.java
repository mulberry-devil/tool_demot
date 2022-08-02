package com.caston.shiro.realm;

import com.caston.shiro.entity.Account;
import com.caston.shiro.service.AccountService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private AccountService accountService;

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取当前登录的用户信息
        Subject subject = SecurityUtils.getSubject();
        Account account = (Account) subject.getPrincipal();

        String[] roles = account.getRole().split(",");
        // 设置角色
        Set<String> rolesSet = Arrays.stream(roles).collect(Collectors.toSet());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(rolesSet);
        String[] perms = account.getPerms().split(",");
        Set<String> permsSet = Arrays.stream(perms).collect(Collectors.toSet());
        // 设置权限
        info.addStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        Account account = accountService.lambdaQuery().eq(Account::getUsername, token.getUsername()).one();
        if (account != null) {
            return new SimpleAuthenticationInfo(account, account.getPassword(), ByteSource.Util.bytes("bjsxd"), getName());
        }
        return null;
    }
}
