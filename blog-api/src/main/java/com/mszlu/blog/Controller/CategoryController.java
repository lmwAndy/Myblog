package com.mszlu.blog.Controller;

import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public Result listCategory(){

        return categoryService.findAllCategory();
    }

    /**
     * 文章分类
     * @return
     */

    @GetMapping("detail")
    public Result categorysDetail(){

        return categoryService.findAllDetail();
    }


    /**
     * 根据分类id，查询分类详情
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result categoryDetailById(@PathVariable("id") Long id){

        return categoryService.categoryDetailById(id);
    }


}
