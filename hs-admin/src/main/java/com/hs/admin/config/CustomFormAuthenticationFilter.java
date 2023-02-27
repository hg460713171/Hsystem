package com.hs.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
@Slf4j
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
//        //获取已登录的用户信息
//        //获取session
        //String activeUser =  subject.getPrincipal();

        log.info("success");
        return super.onLoginSuccess(token, subject, request, response);
    }
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        log.info("failure");
        return super.onLoginFailure(token, e, request, response);
    }
}
