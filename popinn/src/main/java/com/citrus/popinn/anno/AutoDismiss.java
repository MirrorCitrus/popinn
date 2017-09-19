/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import android.support.annotation.IdRes;

/**
 * Created by cdf on 17/3/11.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AutoDismiss {
    int delay() default -1;
    @IdRes int dismissId() default 0;
}
