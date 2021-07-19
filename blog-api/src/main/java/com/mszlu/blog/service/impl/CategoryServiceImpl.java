package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.CategoryMapper;
import com.mszlu.blog.dao.pojo.Category;
import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取文章详情页下的Category
     * @param categoryId
     * @return
     */
    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);

        CategoryVo categoryVo=new CategoryVo();
        //直接copy到categoryVo中
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }

    /**
     * 发布文章，获取所有标签
     * @return
     */
    @Override
    public Result findAllCategory() {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        List<Category> categories = categoryMapper.selectList(queryWrapper);

        return Result.success(copyList(categories));
    }

    /**
     *文章分类页面
     * @return
     */
    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        List<Category> categories = categoryMapper.selectList(queryWrapper);

        //页面交互的对象
        return Result.success(copyList(categories));

    }

    /**
     * 根据分类id，查询分类详情
     * @param id
     * @return
     */
    @Override
    public Result categoryDetailById(Long id) {
        Category category = categoryMapper.selectById(id);

        CategoryVo categoryVo=copy(category);

        return Result.success(category);

    }


    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }

}
