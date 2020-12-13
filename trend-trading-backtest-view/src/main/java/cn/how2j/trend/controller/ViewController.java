package cn.how2j.trend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    //使用thymeleaf映射规则，映射到html

    @GetMapping("/")
    public String view(){
        return "view";
    }
}
