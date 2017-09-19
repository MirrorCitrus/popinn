/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * single pop's callback to indicates a pop show or dismiss。the callback will be removed after onPopClosed() is called.
 * 
 * Created by cdf on 17/8/9.
 */
public interface PopupCallback {
    void onPopShow();
    void onPopClosed();
}
