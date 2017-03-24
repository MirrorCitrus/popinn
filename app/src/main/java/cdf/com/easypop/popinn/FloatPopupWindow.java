/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.popinn;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import cdf.com.easypop.anno.NightMode;

/**
 * Created by cdf on 17/3/11.
 */
public class FloatPopupWindow extends PopupWindow implements View.OnClickListener, PopupWindowCallback,
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
    private String mTag;

    public FloatPopupWindow(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setClippingEnabled(false);
        setOnDismissListener(this);
    }

    public void showAtLocation(View anchorView, PopLayoutParams params, String tag) {
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
        if (mContentAgentView != null) {
            mContentAgentView.removeAllViews();
        }
        setContentView(null);
        if (mFragment != null) {
            mFragment.onDestroyView();
            mFragment.setHost(null);
            mFragment = null;
        }
        mAutoDismiss = -1;
        mDismissId = 0;
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

    public void setNightMode(NightMode.Mode nightMode) {
        mNightMode = nightMode;
    }

    /**
     * show with a delegate to control pop display
     * @param fragment delegate instance
     */
    public void showWithDelegate(View anchorView, PopFragment fragment, String tag) {
        // prepare show
        if (mContentAgentView == null) {
            mContentAgentView = new FloatPopupView(mContext);
        }
        mContentAgentView.removeAllViews();
        super.setContentView(mContentAgentView);
        
        // set fragment instance
        mFragment = fragment;
        fragment.setHost(this);
        
        View contentView = fragment.onCreateView(mContext, mContentAgentView);
        if (contentView != mContentAgentView && contentView.getParent() == null) {
            mContentAgentView.addView(contentView);
        }
        PopLayoutParams params = fragment.onCreatePopLayoutParams();
        showAtLocation(anchorView, params, tag);
    }

    public ViewGroup getContetViewParent() {
        if (mContentAgentView == null) {
            mContentAgentView = new FloatPopupView(mContext);
        }
        mContentAgentView.removeAllViews();
        super.setContentView(mContentAgentView);
        return mContentAgentView;
    }

    public void setPopGlobalCallbacks(List<PopGlobalCallback> popGlobalCallbacks) {
        mPopGlobalCallbacks = popGlobalCallbacks;
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
        mTag = null;
    }
}
