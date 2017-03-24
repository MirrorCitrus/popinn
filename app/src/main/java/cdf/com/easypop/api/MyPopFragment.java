/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import cdf.com.easypop.R;
import cdf.com.easypop.popinn.PopFragment;
import cdf.com.easypop.popinn.PopLayoutParams;
import cdf.com.easypop.util.CdfUtil;

/**
 * Created by cdf on 17/3/18.
 */
public class MyPopFragment extends PopFragment implements View.OnClickListener, View.OnTouchListener {

    private static final int START_WHERE_NONE = 0;
    private static final int START_WHERE_LEFT = 1;
    private static final int START_WHERE_RIGHT = 2;
    private static final int START_WHERE_TOP = 3;
    private static final int START_WHERE_BOTTOM = 4;

    private View mResizeArea;
    private int mInsets;
    private int mStartWhere = START_WHERE_NONE;
    private int mDownX = -1;
    private int mDownY = -1;
    private Rect mResizeAreaBounds = new Rect();
    private int mTouchSlop;
    private Rect mTmpBounds = new Rect();
    private int mPopWinX = 0;
    private int mPopWinY = 0;

    @Override
    public View onCreateView(Context context, ViewGroup parentView) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_delegate, parentView);

        mResizeArea = contentView.findViewById(R.id.resize_area);
        View closeView = contentView.findViewById(R.id.btn_close);
        closeView.setOnClickListener(this);

        mResizeArea.setOnTouchListener(this);
        mInsets = CdfUtil.dip2px(context, 50);
        mTouchSlop = CdfUtil.dip2px(context, 10);

        return contentView;
    }

    @Override
    public PopLayoutParams onCreatePopLayoutParams() {
        PopLayoutParams params = (new PopLayoutParams.Builder()).setLocation(Gravity.CENTER, 0, 0)
                .setDimension(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .build();
        return params;
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onClick(View v) {
        mHost.dismiss();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mStartWhere = findStartWhere(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                handleUp(x, y);
                mStartWhere = START_WHERE_NONE;
                break;
        }
        return true;
    }

    private void handleUp(int x, int y) {
        int delta;
        switch (mStartWhere) {
            case START_WHERE_LEFT:
                delta = x - mDownX;
                mTmpBounds.left = mResizeAreaBounds.left + delta;
                mPopWinX += (delta / 2);
                mHost.update(mPopWinX, mPopWinY, mTmpBounds.width(), mTmpBounds.height());
                break;
            case START_WHERE_RIGHT:
                delta = x - mDownX;
                mTmpBounds.right = mResizeAreaBounds.right + delta;
                mPopWinX += (delta / 2);
                mHost.update(mPopWinX, mPopWinY, mTmpBounds.width(), mTmpBounds.height());

                break;
            case START_WHERE_TOP:
                delta = y - mDownY;
                mTmpBounds.top = mResizeAreaBounds.top + delta;
                mPopWinY += (delta / 2);
                mHost.update(mPopWinX, mPopWinY, mTmpBounds.width(), mTmpBounds.height());
                break;
            case START_WHERE_BOTTOM:
                delta = y - mDownY;
                mTmpBounds.bottom = mResizeAreaBounds.bottom + delta;
                mPopWinY += (delta / 2);
                mHost.update(mPopWinX, mPopWinY, mTmpBounds.width(), mTmpBounds.height());
                break;
            default:
                break;
        }
    }

    private void handleMove(int x, int y) {
        int delta;
        switch (mStartWhere) {
            case START_WHERE_LEFT:
                delta = x - mDownX;
                if (delta > mTouchSlop) {
                    mTmpBounds.left = mResizeAreaBounds.left + delta;
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mResizeArea.getLayoutParams();
                    params.width = mTmpBounds.width();
                    mResizeArea.setLeft(mTmpBounds.left);
                }
                break;
            case START_WHERE_RIGHT:
                delta = x - mDownX;
                if (delta < -mTouchSlop) {
                    mTmpBounds.right = mResizeAreaBounds.right + delta;
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mResizeArea.getLayoutParams();
                    params.width = mTmpBounds.width();
                    mResizeArea.setRight(mTmpBounds.right);
                }
                break;
            case START_WHERE_TOP:
                delta = y - mDownY;
                if (delta > mTouchSlop) {
                    mTmpBounds.top = mResizeAreaBounds.top + delta;
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mResizeArea.getLayoutParams();
                    params.height = mTmpBounds.height();
                    mResizeArea.setTop(mTmpBounds.top);
                }
                break;
            case START_WHERE_BOTTOM:
                delta = y - mDownY;
                if (delta < -mTouchSlop) {
                    mTmpBounds.bottom = mResizeAreaBounds.bottom + delta;
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mResizeArea.getLayoutParams();
                    params.height = mTmpBounds.height();
                    mResizeArea.setBottom(mTmpBounds.bottom);
                }
                break;
            default:
                break;
        }
    }

    private int findStartWhere(int x, int y) {
        mResizeAreaBounds.set(mResizeArea.getLeft(), mResizeArea.getTop(), mResizeArea.getRight(),
                mResizeArea.getBottom());
        mTmpBounds.set(mResizeAreaBounds);
        if (!mResizeAreaBounds.contains(x, y)) {
            return START_WHERE_NONE;
        }
        if (mResizeAreaBounds.width() > 2 * mInsets) {
            if (x < mResizeAreaBounds.left + mInsets) {
                return START_WHERE_LEFT;
            } else if (x > mResizeAreaBounds.right - mInsets) {
                return START_WHERE_RIGHT;
            }
        }
        if (mResizeAreaBounds.height() > 2 * mInsets) {
            if (y < mResizeAreaBounds.top + mInsets) {
                return START_WHERE_TOP;
            } else if (y > mResizeAreaBounds.bottom - mInsets) {
                return START_WHERE_BOTTOM;
            }
        }
        return START_WHERE_NONE;
    }
}
