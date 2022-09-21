package com.caston.shiro.controller;


import com.caston.common.result.Response;
import com.caston.shiro.entity.Account;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/login")
    public Response login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            Account account = (Account) subject.getPrincipal();
            subject.getSession().setAttribute("account", account);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error().message("登录失败");
        }
        return Response.success().message("登录成功");
    }

    @GetMapping("reLogin")
    public String reLogin() {
        return "请登录";
    }

    @RequiresPermissions(value = {"manager:all", "user:select"}, logical = Logical.OR)
    @GetMapping("/perms")
    public String perms() {
        return "perms";
    }

    @RequiresRoles(value = "user")
    @GetMapping("/roles")
    public String roles() {
        return "roles";
    }

    @GetMapping("/unauthc")
    public String unauthc() {
        return "未授权,无法访问";
    }

    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "退出成功";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}