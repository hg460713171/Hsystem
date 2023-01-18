package com.hs.admin.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HsRealm extends AuthorizingRealm {


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return  null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //从被shiro封装成的token中取出我们传入的username
        String username = (String) authenticationToken.getPrincipal();
        //这里应有一步去缓存或数据库查询的步骤，我省略了
        //我直接定义了一个username，如果用户名不匹配，则报错用户名不存在。
        if(!"1".equals(username)){
            throw new UnknownAccountException("账号不存在");
        }
        //返回一个新封装的认证实体，传入的是用户名，数据库查出来的密码，和当前Realm的名字
        return new SimpleAuthenticationInfo(username, "1", this.getName());
    }
}
