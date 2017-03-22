/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.anno;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import android.support.annotation.AnimRes;
import android.support.annotation.StyleRes;

/**
 * Created by cdf on 17/3/11.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface BindAnim {
    @AnimRes int enter() default 0;
    @AnimRes int exit() default 0;
    @StyleRes int animStyle() default 0;
}
