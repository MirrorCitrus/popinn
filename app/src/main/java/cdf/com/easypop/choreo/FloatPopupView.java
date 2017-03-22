package cdf.com.easypop.choreo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cdf.com.easypop.anno.NightMode;

/**
 * Created by cdf on 2017/3/14.
 */
public class FloatPopupView extends FrameLayout {
    private static final int DEFAULT_SHADOW_COLOR = 0x60000000;


    /**
     * 用于夜间模式下图片变暗的参数(需要和内核的算法保持一致)：rgb色值的缩放系数
     */
    public static final float FACTOR_RGB = 0.664f;
    /**
     * 用于夜间模式下图片变暗的参数(需要和内核的算法保持一致)：rgb色值的偏移因数
     */
    public static final float OFFSET_RGB = -33f;

    public static final float[] mColorFilterArray = {
            FACTOR_RGB, 0, 0, 0, OFFSET_RGB,
            0, FACTOR_RGB, 0, 0, OFFSET_RGB,
            0, 0, FACTOR_RGB, 0, OFFSET_RGB,
            0, 0, 0, 1, 0
    };

    private float mScale = 1.0f;
    private static Object sShadowTag;
    private View mShadowView;
    private ColorMatrixColorFilter mColorFilter;

    public FloatPopupView(Context context) {
        super(context);
    }

    public void setScaleRatio(float scale) {
        mScale = scale;
        int childCnt = getChildCount();
        for (int i = 0; i < childCnt; i++) {
            getChildAt(i).setScaleX(scale);
            getChildAt(i).setScaleY(scale);
            getChildAt(i).setPivotX(0);
            getChildAt(i).setPivotY(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width / mScale), widthMode);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heigjtMode = MeasureSpec.getMode(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height / mScale), heigjtMode);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mShadowView != null) {
            View contentView = null;
            int childCnt = getChildCount();
            for (int i = 0; i < childCnt; i++) {
                if (getChildAt(i) != mShadowView) {
                    contentView = getChildAt(i);
                    break;
                }
            }
            if (contentView != null) {
                mShadowView.getLayoutParams().width = contentView.getMeasuredWidth();
                mShadowView.getLayoutParams().height = contentView.getMeasuredHeight();
            }
        }

        setMeasuredDimension((int) (getMeasuredWidth() * mScale), (int) (getMeasuredHeight() * mScale));
    }

    public void setNightMode(NightMode.Mode nightMode, boolean isNight) {
        switch (nightMode) {
            case COVER:
                if (isNight) {
                    addNightShadowView();
                } else {
                    removeNightShadowView();
                }
                break;
            case COLOR_FILTER:
                if (isNight) {
                    addColorFilter();
                } else {
                    removeColorFilter();
                }
                break;
            default:
                break;
        }
    }

    private void removeColorFilter() {
        setColorFilterInner(this, null);
    }

    private void addColorFilter() {
        if (mColorFilter == null) {
            mColorFilter = new ColorMatrixColorFilter(mColorFilterArray);
        }
        setColorFilterInner(this, mColorFilter);
    }

    private void setColorFilterInner(View root, ColorFilter colorFilter) {
        if (root.getBackground() != null) {
            root.getBackground().setColorFilter(colorFilter);
        }
        if (root instanceof ViewGroup) {
            int childCnt = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCnt; i++) {
                setColorFilterInner(((ViewGroup) root).getChildAt(i), colorFilter);
            }
        } else {
            // View instances
            if (root instanceof TextView) {
                int color = ((TextView) root).getCurrentTextColor();
                color = changeColor(color, colorFilter);
                ((TextView) root).setTextColor(color);
            } else if (root instanceof ImageView) {
                Drawable drawable = ((ImageView) root).getDrawable();
                if (drawable != null) {
                    drawable.setColorFilter(colorFilter);
                }
            }
        }
    }

    private int changeColor(int color, ColorFilter colorFilter) {
        if (colorFilter == null) {
            int alpha = Color.alpha(color);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            alpha = (int) ((alpha - OFFSET_RGB) / FACTOR_RGB);
            red = (int) ((red - OFFSET_RGB) / FACTOR_RGB);
            green = (int) ((green - OFFSET_RGB) / FACTOR_RGB);
            blue = (int) ((blue - OFFSET_RGB) / FACTOR_RGB);
            return Color.argb(alpha, red, green, blue);

        } else {
            int alpha = Color.alpha(color);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            alpha = (int) (alpha * FACTOR_RGB + OFFSET_RGB);
            red = (int) (red * FACTOR_RGB + OFFSET_RGB);
            green = (int) (green * FACTOR_RGB + OFFSET_RGB);
            blue = (int) (blue * FACTOR_RGB + OFFSET_RGB);
            return Color.argb(alpha, red, green, blue);
        }
    }

    private void removeNightShadowView() {
        if (mShadowView == null) {
            return;
        }
        removeView(mShadowView);
    }

    private void addNightShadowView() {
        // unexpected error prevention
        removeNightShadowView();

        mShadowView = new View(getContext());
        mShadowView.setBackgroundColor(DEFAULT_SHADOW_COLOR);

        addView(mShadowView);
    }
}
