package com.bkxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bkxt.domain.Category;
import com.bkxt.vo.ArticleListVo;

import java.util.List;

/**
 * @author 35238
 * @date 2023/7/19 0019 22:38
 */
public interface CategoryMapper extends BaseMapper<Category> {
    List<ArticleListVo> serchList(String search);
}