package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.dao.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */

    List<Tag> findTagByarticleId(Long articleId);

    /**
     * 查询最热的标签 所有条
     * @param
     * @return
     */
    List<Long> findHotsTagIds(int limit);

    List<Tag> findTagsByTagsIds(List<Long> hotTagIdslist);
}
