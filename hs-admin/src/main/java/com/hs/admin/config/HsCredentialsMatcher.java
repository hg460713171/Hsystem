package com.hs.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HsCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken tokenResolve = (UsernamePasswordToken) token;
        String tokenPwd = new String(tokenResolve.getPassword());
        String infoPwd =(String) info.getCredentials();
        //调用当前类重写的equals方法来对比两个password是否一致，返回对比结果
        return super.equals(tokenPwd, infoPwd);
    }
}
