package com.hs.admin.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@Slf4j
public class HsLoginController {

    @PostMapping(value = "/doLogin")
    public void doLogin(){
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken("1", "1");
            subject.login(usernamePasswordToken);

            System.out.println("登陆成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("登陆失败");
        }
    }



    //我们可以使用postman进行调用测试 登录前后hello的区别
    @GetMapping(value = "/hello")
    public String hello(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        System.out.println(cookies[0].getValue());
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
