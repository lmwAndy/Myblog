package com.mszlu.blog.service;


import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;

public interface CategoryService{

    /**
     * 文章详情，下的category
     * @param categoryId
     * @return
     */
    CategoryVo findCategoryById(Long categoryId);

    /**
     * 发布文章，获取所有文章分类
     * @return
     */
    Result findAllCategory();

    /**
     * 文章分类页面
     * @return
     */
    Result findAllDetail();

    /**
     * 文章分类详情
     * @param id
     * @return
     */
    Result categoryDetailById(Long id);
}
