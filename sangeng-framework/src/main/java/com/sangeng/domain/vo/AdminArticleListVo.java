package com.sangeng.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminArticleListVo {

    private List<AdminArticleVo> rows;
    private Long total;

}
