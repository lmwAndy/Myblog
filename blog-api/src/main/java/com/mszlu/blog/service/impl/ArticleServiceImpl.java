package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dos.Archives;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.mapper.ArticleTagMapper;
import com.mszlu.blog.dao.pojo.Article;
import com.mszlu.blog.dao.pojo.ArticleBody;
import com.mszlu.blog.dao.pojo.ArticleTag;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.*;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.*;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {


    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Result listArticle(PageParams pageParams) {

        System.out.println(pageParams.getYear()+pageParams.getMonth());
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.listArticle(page,pageParams.getCategoryId(),pageParams.getTagId(),pageParams.getYear(),pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }

//


//    @Override
//    public Result listArticle(PageParams pageParams) {
//
//        /**
//         * 分页查询 article 数据库表
//         */
//        //第一个参数
//        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        //第二个参数,查询条件
//
//        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
//        //是否置顶进行排序
//        //按时间进行排序,道训排列
//        //查询文章的参数 加上分类id，判断不为空 加上分类条件
//
//
//        if (pageParams.getCategoryId() != null) {
//
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        List<Long> articleIdList=new ArrayList<>();
//        if (pageParams.getTagId()!=null){
//            //加入标签条件
//            //article表中 并没有标签字段 一篇文章对应多个标签
//            //article_tag article_id 1 ：n tag_id
//            LambdaQueryWrapper<ArticleTag> queryWrapper1=new LambdaQueryWrapper<>();
//            queryWrapper1.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(queryWrapper1);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList!=null){
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//
//        }
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//
//
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();//获取对象集合
//        List<ArticleVo> articleVoList=copyList(records,true,true);//转移
//        return Result.success(articleVoList);//把转换后的vo返回对象集合传递到结果对象中
//    }

    //下面两个方法是把查询到的实体类集合转换成vo返回对象
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {//是比比那里实体类集合，把集合中的实体类转换成vo返回对象
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }

        return articleVoList;
    }

    //下面两个方法是把查询到的实体类集合转换成vo返回对象
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor, boolean isBody, boolean isCategaory) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {//是比比那里实体类集合，把集合中的实体类转换成vo返回对象
            articleVoList.add(copy(record,isTag,isAuthor,isBody,isCategaory));
        }

        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategaory){
        //把实体类转换成vo类
        ArticleVo articleVo=new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article,articleVo);
        if (isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagByarticleId(articleId));

        }
        if (isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }

        if (isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }

        if (isCategaory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategorys(categoryService.findCategoryById(categoryId));
        }
        //给vo的属性赋值
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        return articleVo;
    }



    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }


    /**
     * 根据浏览记录来找最热文章的前5条
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {

        //查询条件
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //是否置顶进行排序
        //按浏览量进行排序,道训排列
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from ms_article order by viewCounts desc limit 5;
        List<Article> hotArticle=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(hotArticle,false,false));
    }

    /**
     * 最新文章
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //是否置顶进行排序
        //按时间进行排序,道训排列
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from ms_article order by createDate desc limit 5;
        List<Article> newArticle=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(newArticle,false,false));
    }

    /**
     * 文章归档
     * @return
     */
    @Override
    public Result listArchives() {

        List<Archives> archives=articleMapper.listArchives();

        return Result.success(archives);
    }

    /**
     * 文章详情
     * @param id
     * @return
     */
    @Autowired
    private ThreadService threadService;
    @Override
    public Result view(String id) {

        /**
         * 1.根据id查询文章信息
         * 2.根据bodyId和categoryId 去做关联查询
         * 3.返回的是ArticleVo
         */

        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article, true, true,true,true);

        //把更新阅读量的操作放入线程池中去执行，和主线程不想管，无论阅读量更新操作是否成功，都不会影响到阅读文章

        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @Override
    public Result publish(ArticleParam articleParam) {
        /*
        1.构建Article对象

        2.作者id 当前登录用户
        3.标签 要将标签加入到关联列表中
        4.body 内容存储 article bodyId
         */
        SysUser sysUser=UserThreadLocal.get();
        if (sysUser.getId()==null){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        Article article=new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        //插入之后 会生成一个文章id
        this.articleMapper.insert(article);
        //tag
        List<TagVo> tags = articleParam.getTags();
        if (tags!=null){

            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setTagId((Long.parseLong(tag.getId())));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }

        }
        //body
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);


        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        //要放得是文章id
        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }


}
