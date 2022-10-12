package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryByIdVo {

    private Long id;

    //分类名
    private String name;

    //描述
    private String description;
    //状态0:正常,1禁用
    private String status;


}
