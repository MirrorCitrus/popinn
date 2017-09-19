/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * Handle to operate current popupWindow
 * 
 * Created by cdf on 17/3/21.
 */
public interface PopHandle {

    /**
     * dismiss the popupWindow
     */
    void dismiss();

    /**
     * true if the popupWindow is showing
     */
    boolean isShowing();
}
