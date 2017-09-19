/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

/**
 * ParameterHandlers
 * 
 * Created by cdf on 17/3/11.
 */
abstract class ParameterHandler<T> {

    static final int DIMENSION_X = 0;
    static final int DIMENSION_Y = 1;

    /**
     * apply specified parameter to pop layoutparams
     */
    abstract void apply(PopLayoutParams params, T value);

    /**
     * PopLayout parameter handler, apply x, y to PopupWindow
     */
    static class Location extends ParameterHandler<Integer> {

        private final int mDimension;

        Location(int dimension) {
            mDimension = dimension;
        }

        @Override
        void apply(PopLayoutParams params, Integer value) {
            switch (mDimension) {
                case DIMENSION_X:
                    params.locationX = value;
                    break;
                case DIMENSION_Y:
                    params.locationY = value;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Dimension parameter handler, apply width, height to PopupWindow
     */
    static class Dimension extends ParameterHandler<Integer> {

        private final int mDimension;

        Dimension(int dimension) {
            mDimension = dimension;
        }

        @Override
        void apply(PopLayoutParams params, Integer value) {
            switch (mDimension) {
                case DIMENSION_X:
                    params.width = value;
                    break;
                case DIMENSION_Y:
                    params.height = value;
                    break;
                default:
                    break;
            }
        }
    }

    static class Scale extends ParameterHandler<Float> {
        @Override
        void apply(PopLayoutParams params, Float value) {
            params.scale = value;
        }
    }

    static class NightSwitch extends ParameterHandler<Boolean> {
        @Override
        void apply(PopLayoutParams params, Boolean value) {
            params.isNight = value;
        }
    }

    static class Gravity extends ParameterHandler<Integer> {
        @Override
        void apply(PopLayoutParams params, Integer value) {
            params.gravity = value;
        }
    }
}
