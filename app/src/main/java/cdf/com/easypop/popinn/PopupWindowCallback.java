/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.popinn;

/**
 * Created by cdf on 17/3/18.
 */
public interface PopupWindowCallback {
    
    void dismiss();

    void update(int left, int top, int right, int bottom);
}
