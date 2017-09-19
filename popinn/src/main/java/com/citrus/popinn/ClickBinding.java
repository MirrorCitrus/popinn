/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * Simple Binding of OnClickListeners
 * 
 * Created by cdf on 17/4/12.
 */
public class ClickBinding<T> {

    private final int mRange;
    private final T mListener;

    ClickBinding(int range, T listener) {
        mRange = range;
        mListener = listener;
    }

    int getRange() {
        return mRange;
    }

    T getListener() {
        return mListener;
    }
}
