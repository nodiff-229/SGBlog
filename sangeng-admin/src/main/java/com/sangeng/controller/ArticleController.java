package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.UpdateArticleVo;
import com.sangeng.service.ArticleService;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 */
@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TagService tagService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article) {
        return articleService.add(article);
    }

    @GetMapping("/list")
    public ResponseResult listArticle(Long pageNum,
                                      Long pageSize, @RequestParam(required = false) String title, @RequestParam(required = false)String summary) {


        return articleService.listArticle(pageNum, pageSize, title, summary);
    }


    @GetMapping("/{id}")
    public ResponseResult getArticleById(@PathVariable("id")Long id) {

        Article article = articleService.getById(id);
        UpdateArticleVo updateArticleVo = BeanCopyUtils.copyBean(article, UpdateArticleVo.class);

        List<Long> tagsId = articleService.getArticleTagById(id);
        updateArticleVo.setTags(tagsId);
        return ResponseResult.okResult(updateArticleVo);

    }

    @PutMapping()
    public ResponseResult updateArticle(@RequestBody UpdateArticleVo updateArticleVo) {
        Article article = BeanCopyUtils.copyBean(updateArticleVo, Article.class);
        articleService.updateById(article);
        Long id = updateArticleVo.getId();
        List<Long> tags = updateArticleVo.getTags();
        System.out.println(tags);
        articleService.saveArticleTags(id, tags);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteArticle(@PathVariable("id") Long id) {

        articleService.removeById(id);
        return ResponseResult.okResult();
    }


}


