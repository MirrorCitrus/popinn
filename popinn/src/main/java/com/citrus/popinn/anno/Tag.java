/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * tag for specified popupWindow
 * Created by cdf on 17/8/9.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Tag {
}
