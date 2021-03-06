/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import com.citrus.popinn.PopFragment;
import com.citrus.popinn.PopGlobalCallback;
import com.citrus.popinn.PopHandle;
import com.citrus.popinn.PopInn;
import com.citrus.popinn.PopupCallback;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import cdf.com.easypop.MainApplication;
import cdf.com.easypop.model.MyConfig;
import cdf.com.easypop.model.User;
import cdf.com.easypop.util.CdfUtil;

/**
 * 浮层展示的封装类
 * 
 * Created by cdf on 17/3/11.
 */
public class PopServiceWrapper {

    private static PopService sPopService;
    private static PopInn sPopInn;

    private static PopService getService() {
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
                    sPopInn = new PopInn.Builder()
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
        getService().showPopWithDelegate(fragment, fragment);
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
            sPopInn.closePopupWindow();
            sPopInn.detatch();
            sPopInn = null; 
        }
    }

    public static void showClickBinding(View.OnClickListener listener) {
        getService().showClickBindingPop(listener);
    }

    public static void showCheckboxDataBinding() {
        final MyConfig config = new MyConfig(true, false, "test");
        getService().showCheckboxDataBinding(config, Gravity.CENTER, new PopupCallback() {
            @Override
            public void onPopShow() {
                
            }

            @Override
            public void onPopClosed() {
                CdfUtil.log("nowConfig::" + config);
            }
        });
    }

    public static boolean addGlobalPopCallback(PopGlobalCallback callback) {
        return getPopInn().addGlobalCallback(callback);
    }

    public static boolean removeGlobalCallback(PopGlobalCallback callback) {
        return sPopInn != null && sPopInn.removeGlobalCallback(callback);
    }

    public static boolean isShowing() {
        return sPopInn != null && sPopInn.isShowing();
    }

    public static Object getCurrentPopTag() {
        if (sPopInn == null) {
            return null;
        } else {
            return sPopInn.getCurrentPopTag();
        }
    }

    public static void atatch(Context context, View anchorView) {
        getPopInn().attach(context).with(anchorView);
    }

    private static class PopNotifyCallback implements PopGlobalCallback {
        @Override
        public void onPopShow(Object tag) {
            Toast.makeText(MainApplication.getContext(), "OnShow: " + tag, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPopClosed(Object tag) {
            Toast.makeText(MainApplication.getContext(), "onHide: " + tag, Toast.LENGTH_SHORT).show();
        }
    }
}
