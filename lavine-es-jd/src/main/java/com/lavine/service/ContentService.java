package com.lavine.service;

import com.lavine.entity.Content;
import com.lavine.utils.ElasticSearchUtil;
import com.lavine.utils.HtmlParseUtil;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description : 内容Service  //描述
 * @Author : qhm  //作者
 * <p>
 * <p>
 * <p>
 * <p>
 * 0@Date: 2020-12-23 16:40  //时间
 */
@Component
@Log4j2
public class ContentService {


    @Resource(name = "elasticSearchUtil")
    private ElasticSearchUtil elasticSearchUtil;
    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public boolean parseContent(String index, String keyword) {
        boolean flag = false;
        try {
            List<Content> list = HtmlParseUtil.getList(keyword);
            log.info("获取数据条数：{}", list.size());
            boolean b = this.elasticSearchUtil.bulkInsert(index, list);
            if (b) {
                log.info("数据插入成功");
                flag = true;
            } else {
                log.info("数据插入失败");
            }
        } catch (Exception e) {
            log.error("数据插入错误");
        }
        return flag;
    }

    /**
     * 批量获取
     *
     * @param index
     * @param keyword
     * @return
     */
    public boolean parseListContent(String index, String... keyword) {
        boolean flag = false;
        try {
            if (Objects.isNull(keyword) || keyword.length == 0) {
                List<String> goodTypeList = HtmlParseUtil.getGoodTypeList();
                //keyword = new String[goodTypeList.size()];
                keyword = goodTypeList.toArray(keyword);

            }
            flag = false;
            for (String s : keyword) {
                List<Content> list = HtmlParseUtil.getList(s);
                log.info("获取目录：{}，数据条数：{}", s, list.size());
                boolean b = this.elasticSearchUtil.bulkInsert(index, list);
                if (b) {
                    log.info("数据插入成功");
                    flag = true;
                } else {
                    log.info("数据插入失败");
                }
            }
        } catch (Exception e) {
            log.info("插入数据出错");
            e.printStackTrace();

        }
        return flag;
    }


    public List<Map<String, Object>> searchContent(String index, String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 0) {
            pageNo = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.sort("_score", SortOrder.DESC);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");
        TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("name", keyword);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", keyword);
        //TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("name", "正版");
        BoolQueryBuilder should = QueryBuilders.boolQuery().should(matchQueryBuilder);
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(should);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //默认是不会拼接高亮的，需要手动替换
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            if (name != null) {
                Text[] fragments = name.fragments();
                StringBuilder n_name = new StringBuilder();
                for (Text fragment : fragments) {
                    n_name.append(fragment);
                }
                sourceAsMap.put("name", n_name.toString());
            }
            listMap.add(sourceAsMap);
        }
        return listMap;
    }
}
