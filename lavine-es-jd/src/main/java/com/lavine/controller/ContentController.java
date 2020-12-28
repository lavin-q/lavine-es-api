package com.lavine.controller;

import com.lavine.service.ContentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description : 内容Controller  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-23 16:35  //时间
 */
@RestController
@Log4j2
public class ContentController {

    @Resource
    private ContentService contentService;


    @RequestMapping("/parse/{keyword}")
    public Object parseContent(@PathVariable String keyword) {
        return this.contentService.parseContent("jd_good", keyword);
    }

    @RequestMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public Object searchContent(@PathVariable String keyword,
                                @PathVariable int pageNo,
                                @PathVariable int pageSize) throws IOException {
        return this.contentService.searchContent("jd_good", keyword, pageNo, pageSize);
    }


    @RequestMapping("/parse")
    public Object parseListContent() {
        boolean jd_good = this.contentService.parseListContent("jd_good");
        log.info("数据请求结束！");
        return jd_good;
    }
}
