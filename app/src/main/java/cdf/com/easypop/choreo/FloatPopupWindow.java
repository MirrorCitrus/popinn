/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.choreo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.PopupWindow;

import cdf.com.easypop.anno.NightMode;

/**
 * Created by cdf on 17/3/11.
 */
public class FloatPopupWindow extends PopupWindow implements View.OnClickListener {

    private int mAutoDismiss;

    private int mDismissId = 0;

    /**
     * Delay instantiated??
     */
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    
    private Runnable mDelayedDismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };
    private FloatPopupView mContentAgentView;
    private NightMode.Mode mNightMode = NightMode.Mode.NONE;

    public FloatPopupWindow() {
        setClippingEnabled(false);
    }
    
    public FloatPopupWindow(Context context) {
        super(context);
    }

    public FloatPopupWindow(View contentView) {
        super(contentView);
    }

    public FloatPopupWindow(int width, int height) {
        super(width, height);
    }

    public FloatPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public void showAtLocation(View anchorView, PopLayoutParams params) {
        setWidth((int) (params.width * params.scale));
        setHeight((int) (params.height * params.scale));
        mContentAgentView.setScaleRatio(params.scale);
        handleAutoDismiss();
        handleNightMode(params.isNight);
        super.showAtLocation(anchorView, params.gravity, params.locationX, params.locationY);
        super.update((int)(params.width * params.scale), (int)(params.height * params.scale));
    }

    private void handleNightMode(boolean isNight) {
        if (mNightMode == NightMode.Mode.NONE) {
            return;
        }
        mContentAgentView.setNightMode(mNightMode, isNight);
    }

    private void handleAutoDismiss() {
        if (mAutoDismiss > 0) {
            mMainThreadHandler.postDelayed(mDelayedDismissRunnable, mAutoDismiss);
        } else {
            if (mDismissId > 0) {
                View dismissView = getContentView().findViewById(mDismissId);
                if (dismissView != null) {
                    dismissView.setOnClickListener(this);
                }
            }
        }
    }

    public void showAsDropDown(View anchorView, PopLayoutParams params) {
        setWidth(params.width);
        setHeight(params.height);
        handleAutoDismiss();
        super.showAsDropDown(anchorView, params.locationX, params.locationY, params.gravity);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setContentView(null);
        mAutoDismiss = -1;
        mDismissId = 0;
        mMainThreadHandler.removeCallbacks(mDelayedDismissRunnable);
    }

    /**
     * set duration before auto dismiss
     * @param autoDismiss duration, ms
     */
    public void setAutoDismiss(int autoDismiss) {
        mAutoDismiss = autoDismiss;
    }

    public void setDismissId(int dismissId) {
        mDismissId = dismissId;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void setContentView(View view) {
        if (view != null) {
            if (mContentAgentView == null) {
                mContentAgentView = new FloatPopupView(view.getContext());
            }
            mContentAgentView.removeAllViews();
            mContentAgentView.addView(view);
            super.setContentView(mContentAgentView);
        } else {
            if (mContentAgentView != null) {
                mContentAgentView.removeAllViews();
            }
            super.setContentView(null);
        }
    }

    public void setNightMode(NightMode.Mode nightMode) {
        mNightMode = nightMode;
    }
}
