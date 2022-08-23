package com.example.work.controller;

import com.example.work.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RootController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/RootPage.html")
    public String rootPage(){

        return "/root/RootPage";
    }

    @GetMapping(value = "/RootShowUsers")
    public String rootShowUsers(){
        return "/root/RootShowUsers";
    }
    @GetMapping(value = "/ShowLogs")
    public String showLogs(){
        return "/logs";
    }

    @GetMapping(value = "/logout")
    public String logout(){
        return "/index";
    }

}
