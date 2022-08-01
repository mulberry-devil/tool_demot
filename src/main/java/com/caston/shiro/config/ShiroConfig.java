package com.caston.shiro.config;

import com.caston.shiro.realm.AccountRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ShiroConfig {
    @Bean
    public AccountRealm accountRealm() {
        return new AccountRealm();
    }

    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("accountRealm") AccountRealm accountRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(accountRealm);
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(defaultWebSecurityManager);
        HashMap<String, String> map = new HashMap<>();
        map.put("/account/login/**", "anon");
        map.put("/**", "authc");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }
}
