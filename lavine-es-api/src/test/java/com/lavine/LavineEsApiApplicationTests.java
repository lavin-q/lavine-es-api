package com.lavine;

import com.alibaba.fastjson.JSON;
import com.lavine.entity.User;
import org.assertj.core.error.uri.ShouldHaveQuery;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * es 7.6.x高级客户端API测试
 */
@SpringBootTest
class LavineEsApiApplicationTests {

    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient client;


    //测试索引创建
    @Test
    void testCreateIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("lavine_bulk");
        //客户端执行请求,获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //判断索引是否存在
    @Test
    void testGetIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest().indices("lavine1");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        try {
            DeleteIndexRequest lavine1 = new DeleteIndexRequest().indices("lavine1");
            AcknowledgedResponse delete = client.indices().delete(lavine1, RequestOptions.DEFAULT);
            System.out.println(delete.isAcknowledged());
        } catch (Exception e) {
            System.out.println("删除失败");
        }
    }

    //测试添加文档
    @Test
    void testCreateDoc() throws IOException {
        //创建用户
        User lavin_zack = new User("lavin_z", 23);

        //创建请求
        IndexRequest request = new IndexRequest("lavine");

        //规则 put /lavine/_doc/1
        request.id("3");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        IndexRequest source = request.source(JSON.toJSONString(lavin_zack), XContentType.JSON);

        //客户端发送请求,获取响应结果
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());
    }

    //获取文档 get /lavine/_doc/1
    @Test
    void testDocIsExist() throws IOException {
        GetRequest request = new GetRequest("lavine", "1");
        //不获取返回的source
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档信息
    @Test
    void testGetDoc() throws IOException {
        GetRequest request = new GetRequest("lavine", "1");
        request.fetchSourceContext();
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields);
    }

    //更新文档信息
    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("lavine", "1");
        updateRequest.timeout("1s");
        User qhm = new User("qhm", 25);
        updateRequest.doc(JSON.toJSONString(qhm), XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
    }

    //删除文档
    @Test
    void testDeleteDoc() throws IOException {
        //判断文档是否存在
        GetRequest request1 = new GetRequest("lavine", "3");
        boolean exists = client.exists(request1, RequestOptions.DEFAULT);
        if (exists) {
            DeleteRequest request = new DeleteRequest("lavine", "3");
            request.timeout("1s");
            DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
            System.out.println(delete);
        }
    }


    //批量插入文档
    @Test
    void testBulkDoc() throws IOException {
        List<User> userList = new ArrayList<>();
        userList.add(new User("qhm1", 25));
        userList.add(new User("qhm2", 28));
        userList.add(new User("qhm3", 20));
        userList.add(new User("qhm4", 5));
        userList.add(new User("qhm5", 550));
        userList.add(new User("qhm6", 55));

        System.out.println(userList.size());
        //创建索引
        //判断索引是否存在
        GetIndexRequest request1 = new GetIndexRequest().indices("lavine_bulk");
        boolean exists = client.indices().exists(request1, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest("lavine_bulk");
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        }
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        for (int i = 0; i < userList.size(); i++) {
            //批量删除
            //bulkRequest.add(new DeleteRequest());
            //批量更新
            //bulkRequest.add(new UpdateRequest());
            bulkRequest.add(
                    new IndexRequest("lavine_bulk")
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures()); //返回是否失败
    }


    //}

    //查询文档
    //SearchRequest 搜索请求
    //SearchSourceBuilder 搜索条件构造
    //HighlightBuilder 构建高亮
    //QueryBuilder 为所有查询构造器的父类
    //xxxQueryBuilder 为查询构造器的子类
    //查询请求（SearchRequest）->查询体（SearchSourceBuilder）->查询条件体（QueryBuilder）

    @Test
    void testSearchDoc() throws IOException {
        //搜索请求
        SearchRequest searchRequest = new SearchRequest("lavine_bulk");
        //searchRequest.searchType("qhm");

        //搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("age","28");

        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "qhm1");
        BoolQueryBuilder should = QueryBuilders.boolQuery().must(termQueryBuilder).should(termQueryBuilder1);
        sourceBuilder.query(should);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response));
        System.out.println("=============================");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }


    @Test
    void contextLoads() {
    }

}
