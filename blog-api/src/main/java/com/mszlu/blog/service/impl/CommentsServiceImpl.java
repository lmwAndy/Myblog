package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.CommentsMapper;
import com.mszlu.blog.dao.pojo.Comment;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.CommentsService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.CommentVo;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;
import com.mszlu.blog.vo.params.CommentParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    /**
     * 1.根据id查询评论列表，comment中查询
     *2. 根据作者的id查询作者的信息
     * 3.判断，如果level等于1，要去判断它有没有子评论
     * 4，如果有，根据评论id进行查询（parentsId）
     */
    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private SysUserService sysUserService;
    @Override
    public Result findcommentsById(Long articleId) {
        //查询条件
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> commentList = commentsMapper.selectList(queryWrapper);

        List<CommentVo> commentVos=copyList(commentList);


        return Result.success(commentVos);
    }

    /**
     * 增加评论
     * @param commentParams
     * @return
     */
    @Override
    public Result create(CommentParams commentParams) {
        //通过拦截器登录验证之后，获取登录的用户信息
        SysUser sysUser = UserThreadLocal.get();
        if (sysUser==null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        Comment comment = new Comment();
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParams.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParams.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParams.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentsMapper.insert(comment);
        return Result.success(null);
    }

    private List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVos=new ArrayList<>();
        for (Comment comment : commentList) {
            commentVos.add(copy(comment));
        }
        return commentVos;
    }

    private CommentVo copy(Comment comment) {
        //把comments转换成commentVo
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setId(String.valueOf(comment.getId()));
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo=this.sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if (level==1){
            Long id = comment.getId();//作为下层评论的父id
            List<CommentVo> commentVos=findcommentsByparentId(id);
            commentVo.setChildrens(commentVos);

        }
        //子评论，给谁评论 toUser
        if (level>1){
            Long toUid = comment.getToUid();
            UserVo toUserVo=this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }

        return commentVo;
    }

    private List<CommentVo> findcommentsByparentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        List<Comment> commentList = commentsMapper.selectList(queryWrapper);
        List<CommentVo> commentVos=copyList(commentList);
        return commentVos;
    }


}
