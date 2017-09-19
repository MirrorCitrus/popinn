/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * callback to control the popupWindow
 * 
 * Created by cdf on 17/3/18.
 */
public interface PopupWindowCallback {
    
    void dismiss();

    void update(int left, int top, int right, int bottom);
    
    boolean isShowing();
}
