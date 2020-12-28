package com.lavine.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Description : ElasticSearch工具类  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-23 16:33  //时间
 */
@Component
@Log4j2
public class ElasticSearchUtil<obj extends Object> {
    @Resource
    private RestHighLevelClient client;


    public boolean createIndex(String index) throws IOException {
        //判断索引是否存在
        if (isExistIndex(index)) {
            log.info("索引已存在，不需要重复创建");
            return true;
        }

        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);
        //客户端执行请求,获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        //System.out.println(createIndexResponse.isAcknowledged());
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引值
     * @return
     * @throws IOException
     */
    public boolean isExistIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest().indices(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);

    }

    /**
     * 批量插入数据
     *
     * @param index 索引名
     * @param list  数据
     * @return
     * @throws IOException
     */
    public boolean bulkInsert(String index, List<obj> list) throws IOException {
        if (list.size() == 0) {
            log.info("未查询到数据！");
            return true;
        }
        boolean flag = createIndex(index);
        if (flag) {
            log.info("索引{}创建成功", index);
        } else {
            log.info("索引{}创建失败", index);
            return false;
        }
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10m");
        for (int i = 0; i < list.size(); i++) {
            //批量删除
            //bulkRequest.add(new DeleteRequest());
            //批量更新
            //bulkRequest.add(new UpdateRequest());
            bulkRequest.add(
                    new IndexRequest(index)
                            .source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        if (bulkRequest.requests().size() == 0) {
            log.info("未添加IndexRequest！");
            return true;
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
}
