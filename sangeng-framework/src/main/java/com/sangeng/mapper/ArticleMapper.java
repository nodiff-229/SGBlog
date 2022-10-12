package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public  interface ArticleMapper extends BaseMapper<Article> {

    List<Long> getArticleTagIdById(Long id);

    void saveArticleTags(@Param("id") Long id, @Param("tags") List<Long> tags);
}
