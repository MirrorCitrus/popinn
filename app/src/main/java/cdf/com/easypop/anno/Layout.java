/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.anno;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import android.support.annotation.LayoutRes;

/**
 * Created by cdf on 17/3/11.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Layout {
    
    @LayoutRes int value();
}
