package com.lavine.entity;

import lombok.*;

/**
 * @Description : 内容实体  //描述
 * @Author : qhm  //作者
 * @Date: 2020-12-23 16:05  //时间
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Content {

    private String img;

    private String name;

    private String price;
}
