package com.lavine.config;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description : ElasticSearch客户端配置类  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-22 16:10  //时间
 */
@Configuration
public class ElasticSearchClientConfig {


    @Bean(name="restHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                ));
    }
}
