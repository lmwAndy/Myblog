package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.mszlu.blog.dao.mapper.TagMapper;
import com.mszlu.blog.dao.pojo.Tag;
import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    @Override
    public List<TagVo> findTagByarticleId(Long id) {
        List<Tag> tags = tagMapper.findTagByarticleId(id);
        return copyList(tags);
    }

    @Override
    public Result hots(int limit) {
        /**
         * 文章所拥有的文章最多
         * 查询 跟根据 tag_id 分组计数
         * 从大到排列
         *SELECT tag_id FROM ms_article_tag GROUP BY tag_id order BY COUNT(*) DESC LIMIT 2
         */
        List<Long> hotTagIdslist=tagMapper.findHotsTagIds(limit);

        /**
         * 通过hotid来查询tags
         */
        if (CollectionUtils.isEmpty(hotTagIdslist)){
            return Result.success(Collections.emptyList());
        }
        List<Tag> tagList=tagMapper.findTagsByTagsIds(hotTagIdslist);
        return Result.success(tagList);
    }

    /**
     * 文章发布，获取所有标签
     * @return
     */
    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);

        List<TagVo> tagVoList = copyList(tagList);
        return Result.success(tagVoList);

    }

    /**
     * 展示标签页面
     * @return
     */
    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();

        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        List<TagVo> tagVoList = copyList(tagList);
        return Result.success(tagVoList);
    }

    /**
     * 根据tagId来展示文章列表
     * @param id
     * @return
     */
    @Override
    public Result findDetailById(Long id) {

        Tag tag = tagMapper.selectById(id);
        TagVo tagVo=copy(tag);

        return Result.success(tagVo);
    }
}
