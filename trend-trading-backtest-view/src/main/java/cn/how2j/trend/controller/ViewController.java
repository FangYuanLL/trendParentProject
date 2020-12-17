package cn.how2j.trend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    //使用thymeleaf映射规则，映射到html

    @Value("${version}")
    String version;

    @GetMapping("/")
    public String view(Model m){

        m.addAttribute("version", version);
        return "view";
    }
}
