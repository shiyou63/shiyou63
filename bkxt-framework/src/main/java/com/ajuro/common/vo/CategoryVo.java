package com.ajuro.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
//返回给前端的特定字段
public class CategoryVo {

    private Long id;
    private String name;

}