/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

import java.lang.ref.WeakReference;

import android.widget.PopupWindow;

/**
 * Handle of a pop to control its dismiss or check if showing
 * Created by cdf on 17/3/21.
 */
class PopDismissHandle implements PopHandle {

    private WeakReference<PopupWindow> mPopupWindowRef;

    PopDismissHandle(PopupWindow popupWindow) {
        mPopupWindowRef = new WeakReference<PopupWindow>(popupWindow);
    }

    @Override
    public void dismiss() {
        if (mPopupWindowRef != null && mPopupWindowRef.get() != null) {
            mPopupWindowRef.get().dismiss();
        }
        mPopupWindowRef = null;
    }

    @Override
    public boolean isShowing() {
        if (mPopupWindowRef != null && mPopupWindowRef.get() != null) {
            return mPopupWindowRef.get().isShowing();
        }
        return false;
    }
}
