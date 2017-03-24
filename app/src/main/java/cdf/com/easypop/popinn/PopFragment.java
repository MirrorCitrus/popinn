/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.popinn;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by cdf on 17/3/18.
 */
public abstract class PopFragment {

    protected PopupWindowCallback mHost;

    public abstract View onCreateView(Context context, ViewGroup parentView);

    public abstract PopLayoutParams onCreatePopLayoutParams();

    public abstract void onDestroyView();

    final void setHost(PopupWindowCallback popupWindowCallback) {
        mHost = popupWindowCallback;
    }
}
