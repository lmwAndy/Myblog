package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TagService {

    List<TagVo> findTagByarticleId(Long articleId);

    /**
     * 查询最热标签
     * @param limit
     * @return
     */
    Result hots(int limit);

    /**
     * 查询所有文章标签
     * @return
     */
    Result findAll();

    /**
     * 标签页面，标签细节
     * @return
     */
    Result findAllDetail();

    Result findDetailById(Long id);
}
