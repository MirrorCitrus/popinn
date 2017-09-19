/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by cdf on 17/6/29.
 */

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface BindingCheckedChange {
    int ALL_VIEW = 0;
    int range() default ALL_VIEW;
}
