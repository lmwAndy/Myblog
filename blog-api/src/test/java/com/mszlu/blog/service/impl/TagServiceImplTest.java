package com.mszlu.blog.service.impl;

import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceImplTest {

    @Autowired
    private TagService tagService;
    @Test
    public void test1(){
        int limi=6;
        Result hotsTagIds = tagService.hots(limi);
        System.out.println(hotsTagIds);
    }
}