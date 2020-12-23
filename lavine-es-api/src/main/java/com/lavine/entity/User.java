package com.lavine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Description : 用户实体类  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-22 16:51  //时间
 */
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private Integer age;
}
