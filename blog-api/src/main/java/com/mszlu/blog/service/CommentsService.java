package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParams;

/**
 * 展示评论
 */
public interface CommentsService {
    /**
     * 根据文章id来查询评论
     * @param articleId
     * @return
     */
    Result findcommentsById(Long articleId);

    /**
     * 评论
     * @param commentParams
     * @return
     */
    Result create(CommentParams commentParams);
}
