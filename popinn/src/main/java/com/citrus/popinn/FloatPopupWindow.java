/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import com.citrus.popinn.anno.BindingClick;
import com.citrus.popinn.anno.NightMode;

/**
 * PopupWindow for multi-pops
 * 
 * Created by cdf on 17/3/11.
 */
class FloatPopupWindow extends PopupWindow implements View.OnClickListener, PopupWindowCallback,
        PopupWindow.OnDismissListener {

    private Context mContext = null;
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
    private PopFragment mFragment;
    private List<PopGlobalCallback> mPopGlobalCallbacks;
    private Object mTag;
    private List<ClickBinding> mClickBinding;
    private PopupCallback mPopCallback;

    FloatPopupWindow(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setClippingEnabled(false);
        setOnDismissListener(this);
        setBackgroundDrawable(null);
    }

    void showAtLocation(View anchorView, PopLayoutParams params, Object tag) {
        mTag = tag;

        if (params.width == PopLayoutParams.UNSPECIFIED || params.height == PopLayoutParams.UNSPECIFIED) {
            View realContentView = mContentAgentView.getChildAt(0);
            int w = realContentView.getLayoutParams().width;
            int h = realContentView.getLayoutParams().height;

            if (params.width == PopLayoutParams.UNSPECIFIED) {
                params.width = w;
            }

            if (params.height == PopLayoutParams.UNSPECIFIED) {
                params.height = h;
            }
        }
        if (params.width >= 0) {
            setWidth((int) (params.width * params.scale));
        } else {
            setWidth(params.width);
        }
        if (params.height >= 0) {
            setHeight((int) (params.height * params.scale));
        } else {
            setHeight(params.height);
        }
        mContentAgentView.setScaleRatio(params.scale);
        handleAutoDismiss();
        handleNightMode(params.isNight);
        handleClickBinding();
        super.showAtLocation(anchorView, params.gravity, params.locationX, params.locationY);
        super.update((int)(params.width * params.scale), (int)(params.height * params.scale));

        // callbacks
        if (mPopGlobalCallbacks != null) {
            for (PopGlobalCallback callback : mPopGlobalCallbacks) {
                if (callback == null) {
                    continue;
                }
                callback.onPopShow(mTag);
            }
        }
        if (mPopCallback != null) {
            mPopCallback.onPopShow();
        }
    }

    private void handleClickBinding() {
        if (mClickBinding == null || mClickBinding.size() == 0) {
            return;
        }
        for (ClickBinding binding : mClickBinding) {
            switch (binding.getRange()) {
                case BindingClick.ALL_VIEW_HAS_ID:
                    View realContentView = mContentAgentView.getChildAt(0);
                    addBindingClick(realContentView, binding.getListener());
                    break;
                default:
                    break;
            }
        }
    }

    private <T> void addBindingClick(View rootView, T listener) {
        if (listener == null || rootView == null) {
            return;
        }

        if (rootView instanceof ViewGroup) {
            int childCnt = ((ViewGroup) rootView).getChildCount();
            for (int i = 0; i < childCnt; i++) {
                addBindingClick(((ViewGroup) rootView).getChildAt(i), listener);
            }
        } else {
            if (mDismissId != 0 && rootView.getId() != mDismissId) {
                if (listener instanceof View.OnClickListener) {
                    rootView.setOnClickListener((View.OnClickListener) listener);
                } else if (listener instanceof CompoundButton.OnCheckedChangeListener) {
                    if (rootView instanceof CompoundButton) {
                        CompoundButton compoundButton = (CompoundButton) rootView;
                        compoundButton.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) listener);
                    }
                }
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mContentAgentView != null) {
            mContentAgentView.removeAllViews();
        }
        setContentView(null);
        if (mFragment != null) {
            mFragment.destroyView();
            mFragment = null;
        }
        mAutoDismiss = -1;
        mDismissId = 0;
        mClickBinding = null;
        mMainThreadHandler.removeCallbacks(mDelayedDismissRunnable);
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

    /**
     * set duration before auto dismiss
     * @param autoDismiss duration, ms
     */
    void setAutoDismiss(int autoDismiss) {
        mAutoDismiss = autoDismiss;
    }

    void setDismissId(int dismissId) {
        mDismissId = dismissId;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    void setNightMode(NightMode.Mode nightMode) {
        mNightMode = nightMode;
    }

    void setClickBinding(List<ClickBinding> clickBinding) {
        mClickBinding = clickBinding;
    }

    /**
     * show with a delegate to control pop display
     * @param fragment delegate instance
     */
    void showWithDelegate(View anchorView, PopFragment fragment, Object tag) {
        // prepare show
        if (mContentAgentView == null) {
            mContentAgentView = new FloatPopupView(mContext);
        }
        mContentAgentView.removeAllViews();
        super.setContentView(mContentAgentView);

        // set fragment instance
        mFragment = fragment;
        fragment.setHost(this);

        View contentView = fragment.createView(mContext, mContentAgentView);
        if (contentView != mContentAgentView && contentView.getParent() == null) {
            mContentAgentView.addView(contentView);
        }
        PopLayoutParams params = fragment.onCreatePopLayoutParams();
        showAtLocation(anchorView, params, tag);
    }

    ViewGroup getContetViewParent() {
        if (mContentAgentView == null) {
            mContentAgentView = new FloatPopupView(mContext);
        }
        mContentAgentView.removeAllViews();
        super.setContentView(mContentAgentView);
        return mContentAgentView;
    }

    void setPopGlobalCallbacks(List<PopGlobalCallback> popGlobalCallbacks) {
        mPopGlobalCallbacks = popGlobalCallbacks;
    }

    void setPopupCallback(PopupCallback callback) {
        mPopCallback = callback;
    }

    @Override
    public void onDismiss() {
        if (mPopGlobalCallbacks != null) {
            for (PopGlobalCallback callback : mPopGlobalCallbacks) {
                if (callback == null) {
                    continue;
                }
                callback.onPopClosed(mTag);
            }
        }
        if (mPopCallback != null) {
            mPopCallback.onPopClosed();
            mPopCallback = null;
        }
        mTag = null;
    }

    Object getTag() {
        return mTag;
    }
}
