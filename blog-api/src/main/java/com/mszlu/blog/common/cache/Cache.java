package com.mszlu.blog.common.cache;

/**
 * 缓存切点
 */

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    long expire() default 1 * 60 * 1000;

    //缓存标识 key
    String name() default "";

}
