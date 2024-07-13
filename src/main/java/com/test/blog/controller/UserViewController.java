package com.test.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String login() {
        System.out.println("로그인하러가자");
        return "oauthLogin";
    }

    @GetMapping("/signup")
    public String signup() {
        System.out.println("회원가입하러가자");
        return "signup";
    }
}