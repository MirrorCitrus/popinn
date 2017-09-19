/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by cdf on 17/4/12.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface BindingClick {
    int ALL_VIEW_HAS_ID = 0;
    int range() default ALL_VIEW_HAS_ID;
}
