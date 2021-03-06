/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

import android.view.Gravity;

/**
 * LayoutParams of a pop
 * 
 * Created by cdf on 17/3/11.
 */
public class PopLayoutParams {
    static final int UNSPECIFIED = -3;
    int gravity = Gravity.NO_GRAVITY;
    int locationX = 0;
    int locationY = 0;
    int width = UNSPECIFIED;
    int height = UNSPECIFIED;

    float scale = 1;
    boolean isNight = false;
    
    public static class Builder {
        PopLayoutParams params = new PopLayoutParams();
        public Builder setLocation(int gravity, int x, int y) {
            params.gravity = gravity;
            params.locationX = x;
            params.locationY = y;
            return this;
        }
        
        public Builder setDimension(int w, int h) {
            params.width = w;
            params.height = h;
            return this;
        }
        
        public PopLayoutParams build() {
            return params;
        }
    }
}
