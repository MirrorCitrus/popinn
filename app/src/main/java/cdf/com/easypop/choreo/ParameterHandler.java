/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.choreo;

/**
 * Created by cdf on 17/3/11.
 */
public abstract class ParameterHandler<T> {

    public static final int DIMENSION_X = 0;
    public static final int DIMENSION_Y = 1;

    /**
     * apply specified parameter to pop layoutparams
     */
    abstract void apply(PopLayoutParams params, T value);

    /**
     * Location parameter handler, apply x, y to PopupWindow
     */
    public static class Location extends ParameterHandler<Integer> {

        private final int mDimension;

        public Location(int dimension) {
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
    public static class Dimension extends ParameterHandler<Integer> {

        private final int mDimension;

        public Dimension(int dimension) {
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

    public static class Scale extends ParameterHandler<Float> {
        @Override
        void apply(PopLayoutParams params, Float value) {
            params.scale = value;
        }
    }

    public static class NightSwitch extends ParameterHandler<Boolean> {
        @Override
        void apply(PopLayoutParams params, Boolean value) {
            params.isNight = value;
        }
    }
}
