package com.bkxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bkxt.domain.Comment;
import com.bkxt.vo.CommentVo;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author 35238
 * @date 2023/7/24 0024 23:03
 */
//评论表的表数据访问层
public interface CommentMapper extends BaseMapper<Comment> {
    // 查询根评论（带用户信息）
    List<CommentVo> selectRootCommentsWithUser(
            @Param("articleId") Long articleId,
            @Param("commentType") String commentType
    );

    // 查询子评论（带用户信息）
    List<CommentVo> selectChildrenWithUser(
            @Param("parentId") Long parentId
    );
}