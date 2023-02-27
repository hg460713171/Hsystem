package com.hs.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;

import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HsShiroConfig {


    @Autowired
    private CustomFormAuthenticationFilter customFormAuthenticationFilter;

    @Bean
    ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //传入安全管理器
        bean.setSecurityManager(securityManager());
//        //传入未登录用户访问登陆用户的权限所跳转的页面
   //     bean.setLoginUrl("/home/doLogin");
 //       bean.setSuccessUrl("/index");
//
//        //访问未授权网页所跳转的页面
  //     bean.setUnauthorizedUrl("/unauthorized");
//        Map<String, String> map = new LinkedHashMap<>();
//        //允许  需要设置login为anon 否则登陆成功后无法成功跳转。
//        map.put("/login", "anon");
//        map.put("/doLogin", "authc");
//        map.put("/index", "anon");
//        //设置所有的请求未登录不允许进入。
//        map.put("/**", "authc");
//        Map<String, Filter> filters = new HashMap<>();
//        filters.put("cus",customFormAuthenticationFilter);
//        bean.setFilters(filters);
//        map.put("/**", "cus");
//        bean.setFilterChainDefinitionMap(map);
        return bean;
    }
    @Bean
    HsCredentialsMatcher getHsCredentialsMatcher(){
        return new HsCredentialsMatcher();
    }
    @Bean
    HsRealm getRealm(){
        return new HsRealm();
    }


    //引入密码校验
    //配置一个安全管理器
    @Bean
    DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        HsRealm myRealm = getRealm();
        //将我们配置好的密码校验放入域中
        myRealm.setCredentialsMatcher( getHsCredentialsMatcher());
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
        defaultWebSessionManager.setCacheManager(cacheManager());
        defaultWebSessionManager.setSessionDAO(sessionDAO());
        return defaultWebSessionManager;
    }

    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //redis中针对不同用户缓存
        redisCacheManager.setPrincipalIdFieldName("username");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(2000);
        return redisCacheManager;

    }
    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost("127.0.0.1:6379");
        redisManager.setDatabase(0);
        return redisManager;
    }

    @Bean
    public SessionDAO sessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        //session在redis中的保存时间,最好大于session会话超时时间
        redisSessionDAO.setExpire(12000);
        return redisSessionDAO;
    }

    //设置访问拦截器

}
