package com.hs.admin.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Slf4j
public class HsShiroConfig {

    @Autowired
    private SimpleCredentialsMatcher hsCredentialsMatcher;

    @Autowired
    private HsRealm hsRealm;





    //引入密码校验
    //配置一个安全管理器
    @Bean
    DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        HsRealm myRealm = hsRealm;
        //将我们配置好的密码校验放入域中
        myRealm.setCredentialsMatcher(hsCredentialsMatcher);
        //将域添加到我们的安全管理器中
        manager.setRealm(myRealm);
        //设置Session管理器，配置shiro中Session的持续时间
        manager.setSessionManager(getDefaultWebSessionManager());
        SecurityUtils.setSecurityManager(manager);
        return manager;
    }


    //设置session过期时间
    @Bean
    public DefaultWebSessionManager getDefaultWebSessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setGlobalSessionTimeout(1000 * 60);// 会话过期时间，单位：毫秒--->一分钟,用于测试
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        return defaultWebSessionManager;
    }



    //设置访问拦截器
    @Bean
    ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //传入安全管理器
        bean.setSecurityManager(securityManager());
        //传入未登录用户访问登陆用户的权限所跳转的页面
        bean.setLoginUrl("/login");

        //设置成功后返回页面
        bean.setSuccessUrl("/index");

        //访问未授权网页所跳转的页面
        bean.setUnauthorizedUrl("/unauthorized");
        Map<String, String> map = new LinkedHashMap<>();
        //允许  需要设置login为anon 否则登陆成功后无法成功跳转。
        map.put("/login", "anon");
        map.put("/doLogin", "anon");
        map.put("/index", "anon");
        //设置所有的请求未登录不允许进入。
        map.put("/**", "authc");
        bean.setFilterChainDefinitionMap(map);
        return bean;
    }
}
