/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.choreo;

import android.view.Gravity;

/**
 * Created by cdf on 17/3/11.
 */
class PopLayoutParams {
    public static final int UNSPECIFIED = -3;
    int gravity = Gravity.NO_GRAVITY;
    int locationX = 0;
    int locationY = 0;
    int width = UNSPECIFIED;
    int height = UNSPECIFIED;

    float scale = 1;
    public boolean isNight = false;
}
