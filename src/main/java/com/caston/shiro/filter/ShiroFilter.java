package com.caston.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;

public class ShiroFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] rolesArray = (String[]) o;
        //没有角色限制，没有权限访问
        if (rolesArray == null || rolesArray.length == 0) {
            return false;
        }
        for (String role : rolesArray) {
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
