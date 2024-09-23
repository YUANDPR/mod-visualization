package com.yuan.mod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页管理
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @GetMapping
    public String index() {
        return "index";
    }
}
