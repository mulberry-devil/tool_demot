package com.caston.shiro.controller;


import com.caston.shiro.entity.Account;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caston
 * @since 2022-08-01
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/login")
    public String login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            Account account = (Account) subject.getPrincipal();
            subject.getSession().setAttribute("account", account);
        } catch (Exception e) {
            e.printStackTrace();
            return "登录失败";
        }
        return "登录成功";
    }

    @RequiresPermissions({"manager"})
    @GetMapping("/perms")
    public String perms() {
        return "perms";
    }

    @RequiresRoles({"admin"})
    @GetMapping("/roles")
    public String roles() {
        return "roles";
    }

    @GetMapping("/unauthc")
    public String unauthc() {
        return "未授权,无法访问";
    }

    @GetMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "退出成功";
    }
}