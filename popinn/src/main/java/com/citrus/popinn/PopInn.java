/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.citrus.popinn;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.citrus.popinn.anno.AutoDismiss;
import com.citrus.popinn.anno.BindAnim;
import com.citrus.popinn.anno.BindingCheckedChange;
import com.citrus.popinn.anno.BindingClick;
import com.citrus.popinn.anno.BindingVar;
import com.citrus.popinn.anno.Callback;
import com.citrus.popinn.anno.Layout;
import com.citrus.popinn.anno.NightMode;
import com.citrus.popinn.anno.NightSwitch;
import com.citrus.popinn.anno.PopDelegate;
import com.citrus.popinn.anno.PopLayout;
import com.citrus.popinn.anno.Scale;
import com.citrus.popinn.anno.Tag;

/**
 * Main Manager of all the popupWindows
 * 
 * Created by cdf on 17/3/11.
 */
public class PopInn {

    /**
     * Context instance
     */
    private WeakReference<Context> mContext;
    /**
     * Anchor View
     */
    private WeakReference<View> mAnchorView;
    /**
     * PopupWindow instance
     */
    private FloatPopupWindow mPopupWindow;
    /**
     * current handle to dismiss popupWindow
     */
    private PopHandle mPopHandle;
    /**
     * Global callbacks
     */
    private List<PopGlobalCallback> mPopGlobalCallbacks = new ArrayList<>();

    /**
     * Constructor
     */
    private PopInn() {
    }

    public <T> T create(final Class<T> service) {

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] {service},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        // parse annotations and show popup window
                        return showPopupWindow(method, args);
                    }
                });
    }
    
    private void addGlobalCallbacks(List<PopGlobalCallback> popGlobalCallbacks) {
        if (popGlobalCallbacks != null) {
            mPopGlobalCallbacks.addAll(popGlobalCallbacks);
        }
    }

    /**
     * add a global pop callback to listen any popupWindow is shown/dismissed
     * @return true if global pop callback list is changed
     */
    public boolean addGlobalCallback(PopGlobalCallback popGlobalCallback) {
        if (!mPopGlobalCallbacks.contains(popGlobalCallback)) {
            return mPopGlobalCallbacks.add(popGlobalCallback);
        }
        return false;
    }

    /**
     * remove a global pop callback
     * @return true if global pop callback list is changed
     */
    public boolean removeGlobalCallback(PopGlobalCallback popGlobalCallback) {
        return mPopGlobalCallbacks.remove(popGlobalCallback);
    }
    
    public PopInn attach(Context context) {
        mContext = new WeakReference<>(context);
        return this;
    }

    public void detatch() {
        mAnchorView = null;
        mContext = null;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
    }

    public PopInn with(View anchorView) {
        mAnchorView = new WeakReference<>(anchorView);
        return this;
    }

    /**
     * Show a PopupWindow Inner
     */
    private PopHandle showPopupWindow(Method method, Object[] args) {
        
        // dismiss previous pop if exist
        if (mPopHandle != null) {
            mPopHandle.dismiss();
            mPopHandle = null;
        }
        
        Context context = mContext == null ? null : mContext.get();
        View anchorView = mAnchorView == null ? null : mAnchorView.get();
        if (context == null || anchorView == null) {
            return null;
        }

        initPopupWindowIfNeeded(context);

        // parse method annotations
        Annotation[] annotations = method.getAnnotations();
        if (annotations == null) { // no method annotation will return Annotation[0], won't be null
            return null; 
        }
        int layoutId = 0;
        int animStyleId = 0;
        NightMode.Mode nightMode = NightMode.Mode.NONE;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Layout) {
                layoutId = ((Layout) annotation).value();
            } else if (annotation instanceof AutoDismiss) {
                parseAutoDismissAnnotaionAndApply((AutoDismiss) annotation);
            } else if (annotation instanceof BindAnim) {
                animStyleId = ((BindAnim) annotation).animStyle();
            } else if (annotation instanceof NightMode) {
                nightMode = ((NightMode) annotation).value();
            }
        }

        // set properties
        if (mPopupWindow.getAnimationStyle() != animStyleId) {
            mPopupWindow.setAnimationStyle(animStyleId);
            mPopupWindow.update();
        }
        mPopupWindow.setNightMode(nightMode);

        // parse parameter annotations
        PopLayoutParams params = parsePopLayoutParams(method, args);
        // TODO traversal N times...
        PopFragment fragment = getPopDelegateIfExist(method, args);
        Pair<String, Object> bindingVar = getModuleIfExist(method, args);
        List<ClickBinding> clickBinding = getClickBindingIfExist(method, args);
        PopupCallback callback = getPopupCallbackIfExist(method, args);
        Object tag = getPopTagIfExist(method, args);

        if (callback != null) {
            mPopupWindow.setPopupCallback(callback);
        }

        if (clickBinding.size() > 0) {
            mPopupWindow.setClickBinding(clickBinding);
        }

        if (fragment == null) {
            if (bindingVar == null) {
                // inflate view
                LayoutInflater.from(context).inflate(layoutId, mPopupWindow.getContetViewParent());
                mPopupWindow.showAtLocation(anchorView, params, tag == null ? getDefaultTag(method) : tag);
            } else {
                ViewDataBinding binding = getDataBinding(context, layoutId, mPopupWindow.getContetViewParent());

                if (binding == null) {
                    throw new IllegalArgumentException("Data binding instance null! Is DataBindingLibrary not work or"
                            + "layoutId not correct?");
                }

                // TODO maybe more than one??
                setBindingVariable(binding, bindingVar);
                
                mPopupWindow.showAtLocation(anchorView, params, tag == null ? getDefaultTag(method) : tag);
            }

        } else {
            mPopupWindow.showWithDelegate(anchorView, fragment, tag == null ? getDefaultTag(method) : tag);
        }
        
        mPopHandle = new PopDismissHandle(mPopupWindow);
        return mPopHandle;
    }

    private String getDefaultTag(Method method) {
        return method.getName();
    }

    private Object getPopTagIfExist(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
        int parameterCount = parameterAnnotationsArray.length;

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof Tag) {
                    return args[i];
                }
            }
        }
        return null;
    }

    private PopupCallback getPopupCallbackIfExist(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
        int parameterCount = parameterAnnotationsArray.length;

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof Callback) {
                    if (args[i] instanceof PopupCallback) {
                        return (PopupCallback) args[i];
                    }
                }
            }
        }
        return null;
    }

    private List<ClickBinding> getClickBindingIfExist(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
        List<ClickBinding> result = new ArrayList<>();

        int parameterCount = parameterAnnotationsArray.length;

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof BindingClick) {
                    if (args[i] == null || !(args[i] instanceof View.OnClickListener)) {
                        continue;
                    }
                    result.add(new ClickBinding<>(((BindingClick) annotation).range(),
                            (View.OnClickListener) args[i]));
                } else if (annotation instanceof BindingCheckedChange) {
                    if (args[i] == null || !(args[i] instanceof CompoundButton.OnCheckedChangeListener)) {
                        continue;
                    }
                    result.add(new ClickBinding<>(((BindingCheckedChange) annotation).range(),
                            (CompoundButton.OnCheckedChangeListener) args[i]));
                }
            }
        }
        return result;
    }

    private ViewDataBinding getDataBinding(Context context, int layoutId, ViewGroup root) {
        String layoutName = context.getResources().getResourceName(layoutId);
        if (layoutName == null) {
            return null;
        }

        layoutName = getDataBindingClassName(layoutName);

        if (layoutName != null) {
            try {
                // new ViewDataBinding instance
                Class<?> cls = Class.forName(context.getPackageName() + ".databinding." + layoutName);
                Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                Object binding = method.invoke(cls, LayoutInflater.from(context), root, true);
                return (ViewDataBinding) binding;
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | 
                    IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getDataBindingClassName(String layoutName) {
        String tag = ":layout/";
        int index = layoutName == null ? -1 : layoutName.indexOf(tag);
        if (index >= 0) {
            index += tag.length();
            if (index < layoutName.length()) {
                layoutName = layoutName.substring(index);

                String[] segments = layoutName.split("_");
                String seg;
                for (int i = 0; i < segments.length; i++) {
                    seg = segments[i];
                    seg = seg.toLowerCase();
                    if (seg.length() > 0) {
                        seg = Character.toUpperCase(seg.charAt(0)) + seg.substring(1);
                    }
                    segments[i] = seg;
                }

                StringBuilder sb = new StringBuilder();
                for (String segment : segments) {
                    sb.append(segment);
                }
                sb.append("Binding");
                return sb.toString();
            }
        }
        return null;
    }

    private void setBindingVariable(ViewDataBinding binding, Pair<String, Object> bindingVar) {
        Class<?> cls = binding.getClass();
        try {
            String methodName = "set" + bindingVar.first;
            Method method = cls.getDeclaredMethod(methodName, bindingVar.second.getClass());
            method.invoke(binding, bindingVar.second);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
     *
     * @param method method instance
     * @param args   argumentes
     *
     * @return PopLayoutParams instance, or Default instance if not specified
     */
    private PopLayoutParams parsePopLayoutParams(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        int parameterCount = parameterAnnotationsArray.length;
        ParameterHandler[] parameterHandlers = new ParameterHandler[parameterCount];

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof PopLayout.X) {
                    parameterHandlers[i] = new ParameterHandler.Location(ParameterHandler.DIMENSION_X);
                    break;
                } else if (annotation instanceof PopLayout.Y) {
                    parameterHandlers[i] = new ParameterHandler.Location(ParameterHandler.DIMENSION_Y);
                    break;
                } else if (annotation instanceof PopLayout.W) {
                    parameterHandlers[i] = new ParameterHandler.Dimension(ParameterHandler.DIMENSION_X);
                    break;
                } else if (annotation instanceof PopLayout.H) {
                    parameterHandlers[i] = new ParameterHandler.Dimension(ParameterHandler.DIMENSION_Y);
                    break;
                } else if (annotation instanceof PopLayout.G) {
                    parameterHandlers[i] = new ParameterHandler.Gravity();
                    break;
                } else if (annotation instanceof Scale) {
                    parameterHandlers[i] = new ParameterHandler.Scale();
                    break;
                } else if (annotation instanceof NightSwitch) {
                    parameterHandlers[i] = new ParameterHandler.NightSwitch();
                    break;
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

        return params;
    }

    private PopFragment getPopDelegateIfExist(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        int parameterCount = parameterAnnotationsArray.length;

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof PopDelegate) {
                    return (PopFragment) args[i];
                }
            }
        }
        return null;
    }

    private Pair<String, Object> getModuleIfExist(Method method, Object[] args) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        int parameterCount = parameterAnnotationsArray.length;

        for (int i = 0; i < parameterCount; i++) {
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof BindingVar) {
                    return new Pair<>(((BindingVar) annotation).value(), args[i]);
                }
            }
        }
        return null;
    }

    /**
     * init popupWindow and content ViewGroup instance
     */
    private void initPopupWindowIfNeeded(Context context) {
        if (mPopupWindow == null) {
            mPopupWindow = new FloatPopupWindow(context);
            mPopupWindow.setPopGlobalCallbacks(mPopGlobalCallbacks);
            mPopupWindow.setClippingEnabled(false);
        }
    }

    /**
     * Dismiss PopupWindow
     */
    public void closePopupWindow() {
        if (mPopHandle != null) {
            mPopHandle.dismiss();
        }
    }

    /**
     * check if any popupWindow is showing
     */
    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public Object getCurrentPopTag() {
        if (mPopupWindow != null) {
            return mPopupWindow.getTag();
        } else {
            return null;
        }
    }

    /**
     * Builer for PopInn
     */
    public static class Builder {
        private List<PopGlobalCallback> popGlobalCallback;
        
        public Builder addGlobalCallback(PopGlobalCallback popGlobalCallback) {
            if (this.popGlobalCallback == null) {
                this.popGlobalCallback = new ArrayList<PopGlobalCallback>();
            }
            this.popGlobalCallback.add(popGlobalCallback);
            return this;
        }

        public PopInn build() {
            PopInn popInn = new PopInn();
            popInn.addGlobalCallbacks(popGlobalCallback);
            return popInn;
        }
    }
}
