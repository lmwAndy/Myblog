package com.mszlu.blog.Controller;

import com.mszlu.blog.service.CommentsService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long id){


        return commentsService.findcommentsById(id);
    }


    @PostMapping("create/change")
    public Result create(@RequestBody CommentParams commentParams){

        return commentsService.create(commentParams);
    }
}
