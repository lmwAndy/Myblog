package com.mszlu.blog.Controller;


import com.mszlu.blog.dao.pojo.Tag;
import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController//代表返回的是一个json数据
@RequestMapping("tags")
public class TagsController {

    /**
     * 首页，展示最热标签
     */
    @Autowired
    private TagService tagService;
    @GetMapping("hot")
    public Result listHotTag(){

        int limit=6;
        return tagService.hots(limit);
    }

    /**
     * 发布文章，获取所有标签
     */
    @GetMapping
    public Result listTags(){
        return tagService.findAll();
    }

    /**
     * 显示标签页面
     */
    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    /**
     * 根据标签id，显示文章列表
     */
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){

        return tagService.findDetailById(id);
    }
}
