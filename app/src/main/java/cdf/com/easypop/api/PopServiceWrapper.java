/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import cdf.com.easypop.MainApplication;
import cdf.com.easypop.choreo.Choreo;

/**
 * Created by cdf on 17/3/11.
 */
public class PopServiceWrapper {

    private static PopService sPopService;
    private static Choreo sChoreo;

    public static PopService getService() {
        if (sPopService == null) {
            synchronized(PopServiceWrapper.class) {
                if (sPopService == null) {
                    sPopService = getChoreo().create(PopService.class);
                }
            }
        }
        return sPopService;
    }

    private static Choreo getChoreo() {
        if (sChoreo == null) {
            synchronized(PopServiceWrapper.class) {
                if (sChoreo == null) {
                    sChoreo = new Choreo(MainApplication.getContext());
                }
            }
        }
        return sChoreo;
    }

    /**
     * show a full screen popupWindow, autoDissmiss with specified delay
     */
    public static void showGeneralPop() {
        getService().showGeneralPop();
    }

    /**
     * show a popup window with specified dimension, dismiss when specified view clicked
     */
    public static void showGeneralPop2() {
        getService().showGeneralPop2(100, 300, 800, 800);
    }

    public static void showAnimatedPop() {
        getService().showAnimatedPop();
    }

    public static void closePopupWindow() {
        if (sChoreo != null) {
            sChoreo.closePopupWindow();
        }
    }

    public static void showNightModePop(boolean isNight) {
        getService().showNightModePop(isNight);
    }

    public static void showScaledPop(float scale) {
        getService().showScaledPop(scale, 20, 400, 800, 800);
    }

    public static void showNightModePop2(boolean isNight) {
        getService().showNightModePop2(isNight);
    }
}
