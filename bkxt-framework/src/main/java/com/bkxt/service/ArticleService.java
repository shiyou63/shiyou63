package com.bkxt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bkxt.domain.Article;
import com.bkxt.domain.ResponseResult;
import com.bkxt.dto.AddArticleDto;
import com.bkxt.dto.ArticleDto;
import com.bkxt.vo.ArticleByIdVo;
import com.bkxt.vo.PageVo;

/**
 * @author 35238
 * @date 2023/7/18 0018 21:16
 */
public interface ArticleService extends IService<Article> {

    //文章列表
    ResponseResult hotArticleList();

    //分类查询文章列表
    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    //根据id查询文章详情
    ResponseResult getArticleDetail(Long id);

    //根据文章id从mysql查询文章
    ResponseResult updateViewCount(Long id);

    //新增博客文章
    ResponseResult add(AddArticleDto article);
    //管理后台(文章管理)-分页查询文章
    PageVo selectArticlePage(Article article, Integer pageNum, Integer pageSize);
    //修改文章-①根据文章id查询对应的文章
    ArticleByIdVo getInfo(Long id);

    //修改文章-②然后才是修改文章
    void edit(ArticleDto article);
}