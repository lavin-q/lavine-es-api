package com.lavine.utils;

import com.lavine.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description : hrml页面解析  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-23 15:30  //时间
 */
public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
        /*List<Content> goods = getList("心理学");
        System.out.println(goods.size());
        for (Content content : goods) {
            System.out.println(content);
        }*/
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int k = random.nextInt(32);
            System.out.println(k);
        }

        Random random1 = new Random();
        int j = random1.nextInt(16);
        System.out.println(j);
    }

    /**
     * 获取商品信息
     *
     * @param keyword
     * @return
     * @throws Exception
     */
    public static List<Content> getList(String keyword) throws Exception {
        List<Content> goodsList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            String url = "https://search.jd.com/Search?keyword=" + keyword + "&page=" + i;
            String decode = URLDecoder.decode(url, "UTF-8");
            //解析网页，返回浏览器Document对象
            Document document = Jsoup.parse(new URL(decode), 30000);
            Element element = document.getElementById("J_goodsList");
            //System.out.println(element.html());
            //获取li标签
            if (element == null) {
                continue;
            }
            Elements elements = element.getElementsByTag("li");
            if (elements == null) {
                continue;
            }
            for (Element el : elements) {
                Content content = new Content();
                //String img = el.getElementsByClass("p-img").text();
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                String price = el.getElementsByClass("p-price").eq(0).text();
                String name = el.getElementsByClass("p-name").eq(0).text();
                content.setImg(img);
                content.setPrice(price);
                content.setName(name);
                goodsList.add(content);
            }
        }
        return goodsList;
    }


    //获取京东首页商品分类信息
    public static List<String> getGoodTypeList() throws Exception {
        List<String> goodTypeList = new ArrayList<>();
        String url = "https://www.jd.com/";
        String decode = URLDecoder.decode(url, "UTF-8");
        //解析网页，返回浏览器Document对象
        Document document = Jsoup.parse(new URL(decode), 30000);
        Element element = document.getElementById("J_cate");
        Elements elements = element.getElementsByTag("li");
        for (Element ele : elements) {
            Elements href = ele.getElementsByTag("href");
            String text1 = href.text();
            System.out.println(text1);
            String text = ele.text();
            String[] split = text.split("/");
            for (String s : split) {
                String trim = s.trim();
                goodTypeList.add(trim);
            }

        }
        return goodTypeList;
    }


}
