package com.lavine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.net.URLDecoder;

@SpringBootTest
class LavineEsJdApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void getGoodSType() throws Exception {
        String url = "https://www.jd.com/";
        String decode = URLDecoder.decode(url, "UTF-8");
        //解析网页，返回浏览器Document对象
        Document document = Jsoup.parse(new URL(decode), 30000);
        Element element = document.getElementById("J_cate");
        Elements elements = element.getElementsByTag("li");
        for (Element ele : elements) {
            String href = ele.getElementsByTag("a").attr("href");
            System.out.println(href);
            String deurl = "https:" + href;
            String deDecode = URLDecoder.decode(deurl, "UTF-8");
            //解析网页，返回浏览器Document对象
            Document deDocument = Jsoup.parse(new URL(deDecode), 30000);

            Elements item_title_txt = deDocument.getElementsByClass("item_title_txt");
            System.out.println(item_title_txt.text());
            Elements item_children_item = deDocument.getElementsByClass("item_children_item");
            System.out.println(item_children_item.text());
           /* String text = ele.text();
            String[] split = text.split("/");*/
           /* for (String s : split) {
                String trim = s.trim();
                //goodTypeList.add(trim);
            }*/

        }
        //System.out.println(element);
    }

}
