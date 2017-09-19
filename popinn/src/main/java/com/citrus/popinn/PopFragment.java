/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment of a pop which controls its lifecycle
 * 
 * Created by cdf on 17/3/18.
 */
public abstract class PopFragment {

    protected PopupWindowCallback mHost;

    /**
     * if current PopFragment isShowing, true between onCreateView and onDestroyView
     */
    private boolean mIsShowing;

    /**
     * Called to have the PopFragment instantiate its user interface view.
     * @param context instance of the ui context
     * @param parentView Optional view to be the parent of the generated hierarchy
     * @return The root View of the inflated hierarchy
     */
    public abstract View onCreateView(Context context, ViewGroup parentView);

    /**
     * Called to have the popLayoutParams, which is used to locate the fragment
     * @return instance of the PopLayoutParams
     */
    public abstract PopLayoutParams onCreatePopLayoutParams();

    /**
     * Called When the popupWindow is dismissed
     */
    public abstract void onDestroyView();

    /**
     * Return true if the popupWindow is showing the fragment
     */
    public boolean isShowing() {
        return mIsShowing && (mHost != null && mHost.isShowing());
    }

    final void setHost(PopupWindowCallback popupWindowCallback) {
        mHost = popupWindowCallback;
    }

    void destroyView() {
        mIsShowing = false;
        setHost(null);
        onDestroyView();
    }

    public View createView(Context context, ViewGroup parentView) {
        mIsShowing = true;
        return onCreateView(context, parentView);
    }
}
