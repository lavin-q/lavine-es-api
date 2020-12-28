package com.lavine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description : 初始访问  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-23 11:02  //时间
 */
@Controller
public class IndexController {

    @RequestMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
