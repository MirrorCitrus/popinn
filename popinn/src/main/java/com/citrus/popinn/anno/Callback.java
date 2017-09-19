/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * the callback to notify the pop is showing or dismissed
 * Created by cdf on 17/8/8.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Callback {
}
