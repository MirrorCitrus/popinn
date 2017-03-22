/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.choreo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cdf.com.easypop.MainActivity;
import cdf.com.easypop.anno.AutoDismiss;
import cdf.com.easypop.anno.BindAnim;
import cdf.com.easypop.anno.Layout;
import cdf.com.easypop.anno.LayoutDimension;
import cdf.com.easypop.anno.LocX;
import cdf.com.easypop.anno.LocY;
import cdf.com.easypop.anno.NightMode;
import cdf.com.easypop.anno.NightSwitch;
import cdf.com.easypop.anno.PopH;
import cdf.com.easypop.anno.PopW;
import cdf.com.easypop.anno.Scale;

/**
 * Created by cdf on 17/3/11.
 */
public class Choreo {

    /**
     * Context instance
     */
    private final Context mContext;
    /**
     * PopupWindow instance
     */
    private FloatPopupWindow mPopupWindow;

    /**
     * Constructor
     * @param context Application Context Instance
     */
    public Choreo(Context context) {
        mContext = context;
    }

    public <T> T create(final Class<T> service) {
        
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {

                    @Override public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        // parse annotations and show popup window
                        showPopupWindow(method, args);
                        return null;
                    }
                });
    }

    /**
     * Show a PopupWindow Inner
     */
    private void showPopupWindow(Method method, Object[] args) {

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        
        initPopupWindowIfNeeded();


        // parse method annotations
        Annotation[] annotations = method.getAnnotations();
        if(annotations == null) {
            return;
        }
        int layoutId = 0;
        int animStyleId = 0;
        LayoutDimension.LayoutDimensionMode mode = null;
        NightMode.Mode nightMode = NightMode.Mode.NONE;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Layout) {
                layoutId = ((Layout) annotation).value();
            } else if (annotation instanceof AutoDismiss) {
                parseAutoDismissAnnotaionAndApply((AutoDismiss) annotation);
            } else if (annotation instanceof BindAnim) {
                animStyleId = ((BindAnim)annotation).animStyle();
            } else if (annotation instanceof LayoutDimension) {
                mode = ((LayoutDimension)annotation).value();
            } else if (annotation instanceof NightMode) {
                nightMode = ((NightMode)annotation).value();

            }
        }

        // inflate view
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId, null);

        // parse parameter annotations
        PopLayoutParams params = parsePopLayoutParams(method, args, mode);
        
        // set properties and show
        if (mPopupWindow.getAnimationStyle() != animStyleId) {
            mPopupWindow.setAnimationStyle(animStyleId);
            mPopupWindow.update();
        }
        mPopupWindow.setNightMode(nightMode);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(MainActivity.getTestView(), params);
    }

    private void parseAutoDismissAnnotaionAndApply(AutoDismiss annotation) {
        int autoDismiss = annotation.delay();
        if (autoDismiss > 0) {
            mPopupWindow.setAutoDismiss(autoDismiss);
        } else {
            int dismissId = annotation.dismissId();
            if (dismissId > 0) {
                mPopupWindow.setDismissId(dismissId);
            }
        }
    }

    /**
     * parse parameter annotations and get PopLayoutParams
     * @param method method instance
     * @param args argumentes
     * @return PopLayoutParams instance, or Default instance if not specified
     */
    private PopLayoutParams parsePopLayoutParams(Method method, Object[] args, LayoutDimension.LayoutDimensionMode mode) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        int parameterCount = parameterAnnotationsArray.length;
        ParameterHandler[] parameterHandlers = new ParameterHandler[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof LocX) {
                    parameterHandlers[i] = new ParameterHandler.Location(ParameterHandler.DIMENSION_X);
                    break;
                } else if (annotation instanceof LocY) {
                    parameterHandlers[i] = new ParameterHandler.Location(ParameterHandler.DIMENSION_Y);
                    break;
                } else if (annotation instanceof PopW) {
                    parameterHandlers[i] = new ParameterHandler.Dimension(ParameterHandler.DIMENSION_X);
                    break;
                } else if (annotation instanceof PopH) {
                    parameterHandlers[i] = new ParameterHandler.Dimension(ParameterHandler.DIMENSION_Y);
                    break;
                } else if (annotation instanceof Scale) {
                    parameterHandlers[i] = new ParameterHandler.Scale();
                    break;
                } else if (annotation instanceof NightSwitch) {
                    parameterHandlers[i] = new ParameterHandler.NightSwitch();
                    break;
                } else {
                    continue;
                }
            }
        }

        PopLayoutParams params = new PopLayoutParams();
        for (int i = 0; i < parameterCount; i++) {
            ParameterHandler handler = parameterHandlers[i];
            if (handler == null) {
                continue;
            }
            handler.apply(params, args[i]);
        }

        int layoutMode = ViewGroup.LayoutParams.MATCH_PARENT;
        if (mode == LayoutDimension.LayoutDimensionMode.WRAP_CONTENT) {
            layoutMode = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (params.width == PopLayoutParams.UNSPECIFIED) {
            params.width = layoutMode;
        }

        if (params.height == PopLayoutParams.UNSPECIFIED) {
            params.height = layoutMode;
        }

        return params;
    }

    /**
     * init popupWindow and content ViewGroup instance
     */
    private void initPopupWindowIfNeeded() {
        if (mPopupWindow == null) {
            mPopupWindow = new FloatPopupWindow();
        }
    }

    /**
     * Dismiss PopupWindow
     */
    public void closePopupWindow() {
        mPopupWindow.dismiss();
    }
}
