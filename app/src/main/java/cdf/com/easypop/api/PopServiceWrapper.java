/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import android.view.Gravity;
import android.widget.Toast;
import cdf.com.easypop.MainActivity;
import cdf.com.easypop.MainApplication;
import cdf.com.easypop.popinn.PopFragment;
import cdf.com.easypop.popinn.PopGlobalCallback;
import cdf.com.easypop.popinn.PopHandle;
import cdf.com.easypop.popinn.PopInn;

/**
 * Created by cdf on 17/3/11.
 */
public class PopServiceWrapper {

    private static PopService sPopService;
    private static PopInn sPopInn;

    public static PopService getService() {
        if (sPopService == null) {
            synchronized(PopServiceWrapper.class) {
                if (sPopService == null) {
                    sPopService = getPopInn().create(PopService.class);
                }
            }
        }
        return sPopService;
    }

    private static PopInn getPopInn() {
        if (sPopInn == null) {
            synchronized(PopServiceWrapper.class) {
                if (sPopInn == null) {
                    sPopInn = new PopInn.Builder().context(MainApplication.getContext())
                    .anchorView(MainActivity.getAnchorView())
                    .addGlobalCallback(new PopNotifyCallback())
                    .build();
                }
            }
        }
        return sPopInn;
    }

    public static void closePopupWindow() {
        if (sPopInn != null) {
            sPopInn.closePopupWindow();
        }
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

    public static void showNightModePop(boolean isNight) {
        getService().showNightModePop(isNight);
    }

    public static void showScaledPop(float scale) {
        getService().showScaledPop(scale, 20, 400, 800, 800);
    }

    public static void showNightModePop2(boolean isNight) {
        getService().showNightModePop2(isNight);
    }

    public static void showPopWithDelegate() {
        PopFragment fragment = new MyPopFragment();
        getService().showPopWithDelegate(fragment);
    }

    public static void showPopWithDataBinding() {
        User user = new User("Daniel", 21, "Nanjing", "13188899443");
        getService().showDataBindingPop(user);
    }

    public static void showPopWithGravity(int gravity) {
        getService().showPopWithGravity(gravity);
    }

    public static PopHandle showPopWithoutAutoDismiss() {
        return getService().showPopWithoutAutoDismiss(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }

    public static void release() {
        if (sPopInn != null) {
            sPopInn = null; // release anchor view
        }
    }

    private static class PopNotifyCallback implements PopGlobalCallback {
        @Override
        public void onPopShow(String tag) {
            Toast.makeText(MainApplication.getContext(), "OnShow: " + tag, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPopClosed(String tag) {
            Toast.makeText(MainApplication.getContext(), "onHide: " + tag, Toast.LENGTH_SHORT).show();
        }
    }
}
