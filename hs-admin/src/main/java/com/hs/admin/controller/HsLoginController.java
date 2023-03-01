package com.hs.admin.controller;

import com.hs.admin.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/home")
@Slf4j
public class HsLoginController {

    @PostMapping(value = "/doLogin")
    @ResponseBody
    public void doLogin(@RequestBody UserInfo user){
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            subject.login(usernamePasswordToken);
            log.info("登陆成功");
            HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();;
            request.getSession().setAttribute("123","123");

        }catch (Exception e){
            log.error("登陆失败");
        }
    }
    @PostMapping(value = "/getInfo")
    public void getInfo(){
        HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();;
        request.getCookies();

    }



    //我们可以使用postman进行调用测试 登录前后hello的区别
    @PostMapping(value = "/hello")
    public String hello(HttpServletRequest request){
        //Cookie[] cookies = request.getCookies();
       // System.out.println(cookies[0].getValue());
        return "hello";
    }
    //用来设置未登录用户跳转的方法
    @GetMapping(value = "/login")
    public String login(){
        return "Please Login !";
    }
    //注销方法
    @GetMapping(value = "/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        System.out.println("成功退出");
        return "success to logout";
    }
}
