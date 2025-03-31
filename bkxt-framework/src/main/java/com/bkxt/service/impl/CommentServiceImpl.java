package com.bkxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bkxt.constants.SystemCanstants;
import com.bkxt.domain.Comment;
import com.bkxt.domain.ResponseResult;
import com.bkxt.domain.User;
import com.bkxt.enums.AppHttpCodeEnum;
import com.bkxt.exception.SystemException;
import com.bkxt.mapper.CommentMapper;
import com.bkxt.service.CommentService;
import com.bkxt.service.UserService;
import com.bkxt.utils.BeanCopyUtils;
import com.bkxt.vo.CommentVo;
import com.bkxt.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 35238
 * @date 2023/7/24 0024 23:08
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Autowired  // 新增Redis模板
    private RedisTemplate<String, String> redisTemplate;

    // 查询评论列表（简化排序逻辑 + 分页校验）
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        // 1. 参数校验（新增）
        pageNum = pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize > 100 ? 100 : pageSize;

        // 2. 构建查询条件（数据库排序代替内存排序）
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getType, commentType)
                .eq(Comment::getRootId, SystemCanstants.COMMENT_ROOT) // 修正常量类名拼写
                .eq(SystemCanstants.ARTICLE_COMMENT.equals(commentType), Comment::getArticleId, articleId)
                .orderByDesc(Comment::getCreateTime); // 数据库排序

        // 3. 分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        // 4. 转换VO并处理子评论
        List<Comment> comments = page.getRecords();
        List<CommentVo> commentVos = comments.stream()
                .map(comment -> {
                    CommentVo vo = BeanCopyUtils.copyBean(comment, CommentVo.class);
                    User user = userService.getById(comment.getCreateBy());
                    vo.setUsername(user != null ? user.getNickName() : "匿名用户");
                    vo.setAvatar(user != null ? user.getAvatar() : "/default-avatar.png");
                    return vo;
                })
                .collect(Collectors.toList());

        commentVos.forEach(vo -> vo.setChildren(getChildrenWithUser(vo.getId())));
        return ResponseResult.okResult(new PageVo(commentVos, page.getTotal()));
    }

    // 添加评论（包含简化的频率限制）
    @Override
    public ResponseResult addComment(Comment comment) {
        // 1. 基础校验
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }

        // 2. 频率限制（简化版Redis实现）
        Long userId = comment.getCreateBy();
        String key = "comment:limit:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count > 3) { // 每分钟限制3条
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            throw new SystemException(AppHttpCodeEnum.COMMENT_FREQUENCY_LIMIT.getCode(),
                    "请等待 " + (expire != null ? expire : 60) + "秒后重试");
        }
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);

        // 3. 保存评论
        return ResponseResult.okResult(save(comment));
    }

    // 删除评论（增加级联删除）
    @Override
    public ResponseResult deleteComment(Long commentId) {
        // 安全获取当前用户ID（从Spring Security上下文）
        Long currentUserId = getCurrentUserId();

        Comment comment = getById(commentId);
        if (comment == null) {
            throw new SystemException(AppHttpCodeEnum.COMMENT_NOT_EXIST);
        }

        // 权限校验：仅允许删除自己的评论
        if (!comment.getCreateBy().equals(currentUserId)) {
            throw new SystemException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }

        // 删除逻辑（同之前）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId, commentId)
                .or()
                .eq(Comment::getRootId, commentId);
        baseMapper.delete(wrapper);

        return ResponseResult.okResult();
    }

    // 安全获取用户ID的方法
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }
        return Long.parseLong(authentication.getName()); // 假设用户ID存储在Principal的name中
    }
    // 获取子评论（保持原有逻辑）
    private List<CommentVo> getChildrenWithUser(Long parentId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, parentId)
                .orderByAsc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);

        return comments.stream()
                .map(comment -> {
                    CommentVo vo = BeanCopyUtils.copyBean(comment, CommentVo.class);

                    // 补充子评论用户信息
                    User user = userService.getById(comment.getCreateBy());
                    vo.setUsername(user != null ? user.getNickName() : "匿名用户");
                    vo.setAvatar(user != null ? user.getAvatar() : "/default-avatar.png");

                    return vo;
                })
                .collect(Collectors.toList());
    }
    //封装响应返回。CommentVo、BeanCopyUtils、ResponseResult、PageVo是我们写的类
    private List<CommentVo> xxToCommentList(List<Comment> list){
        //获取评论区的所有评论
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历(可以用for循环，也可以用stream流)。由于封装响应好的数据里面没有username字段，所以我们还不能返回给前端。这个遍历就是用来得到username字段
        for (CommentVo commentVo : commentVos) {
            //
            //需要根据commentVo类里面的createBy字段，然后用createBy字段去查询user表的nickname字段(子评论的用户昵称)
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            //然后把nickname字段(发这条子评论的用户昵称)的数据赋值给commentVo类的username字段
            commentVo.setUsername(nickName);

            //查询根评论的用户昵称。怎么判断是根评论的用户呢，判断toCommentId为1，就表示这条评论是根评论
            if(commentVo.getToCommentUserId() != -1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                //然后把nickname字段(发这条根评论的用户昵称)的数据赋值给commentVo类的toCommentUserName字段
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }

        //返回给前端
        return commentVos;
    }
}
