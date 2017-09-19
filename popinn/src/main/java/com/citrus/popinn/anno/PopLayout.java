/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn.anno;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by cdf on 17/3/20.
 */

public interface PopLayout {
    @Target(PARAMETER)
    @Retention(RUNTIME)
    public @interface X {
    }

    @Target(PARAMETER)
    @Retention(RUNTIME)
    public @interface Y {
    }

    @Target(PARAMETER)
    @Retention(RUNTIME)
    public @interface W {
    }


    @Target(PARAMETER)
    @Retention(RUNTIME)
    public @interface H {
    }

    @Target(PARAMETER)
    @Retention(RUNTIME)
    public @interface G {
    }
}
