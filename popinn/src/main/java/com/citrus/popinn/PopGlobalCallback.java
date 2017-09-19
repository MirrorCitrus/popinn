/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * Global Callback of the popInn, which indicates a pop show or dismiss, along with a tag of the pop
 * 
 * Created by cdf on 17/3/21.
 */
public interface PopGlobalCallback {
    void onPopShow(Object tag);
    void onPopClosed(Object tag);
}
