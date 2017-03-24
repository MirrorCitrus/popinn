/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.popinn;

import java.lang.annotation.Annotation;
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
import cdf.com.easypop.anno.AutoDismiss;
import cdf.com.easypop.anno.BindAnim;
import cdf.com.easypop.anno.BindingVar;
import cdf.com.easypop.anno.Layout;
import cdf.com.easypop.anno.NightMode;
import cdf.com.easypop.anno.NightSwitch;
import cdf.com.easypop.anno.PopDelegate;
import cdf.com.easypop.anno.PopLayout;
import cdf.com.easypop.anno.Scale;

/**
 * Created by cdf on 17/3/11.
 */
public class PopInn {

    /**
     * Context instance
     */
    private final Context mContext;
    /**
     * Anchor View
     */
    private final View mAnchorView;
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
    private List<PopGlobalCallback> mPopGlobalCallbacks;

    /**
     * Constructor
     *
     * @param context Application Context Instance
     * @param anchorView
     */
    public PopInn(Context context, View anchorView) {
        mContext = context;
        mAnchorView = anchorView;
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

    private void setGlobalCallbacks(List<PopGlobalCallback> popGlobalCallbacks) {
        mPopGlobalCallbacks = popGlobalCallbacks;
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

        initPopupWindowIfNeeded();

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

        if (fragment == null) {
            if (bindingVar == null) {
                // inflate view
                LayoutInflater.from(mContext).inflate(layoutId, mPopupWindow.getContetViewParent());
                mPopupWindow.showAtLocation(mAnchorView, params, method.getName());
            } else {
                ViewDataBinding binding = getDataBinding(layoutId, mPopupWindow.getContetViewParent());

                if (binding == null) {
                    throw new IllegalArgumentException("Data binding instance null! Is DataBindingLibrary not work or"
                            + "layoutId not correct?");
                }

                // TODO maybe more than one??
                setBindingVariable(binding, bindingVar);
                
                mPopupWindow.showAtLocation(mAnchorView, params, method.getName());
            }

        } else {
            mPopupWindow.showWithDelegate(mAnchorView, fragment, method.getName());
        }
        
        mPopHandle = new PopDismissHandle(mPopupWindow);
        return mPopHandle;
    }

    private ViewDataBinding getDataBinding(int layoutId, ViewGroup root) {
        String layoutName = mContext.getResources().getResourceName(layoutId);
        if (layoutName == null) {
            return null;
        }

        layoutName = getDataBindingClassName(layoutName);

        if (layoutName != null) {
            try {
                // new ViewDataBinding instance
                Class cls = Class.forName(mContext.getPackageName() + ".databinding." + layoutName);
                Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                Object binding = method.invoke(cls, LayoutInflater.from(mContext), root, true);
                return (ViewDataBinding) binding;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
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
        bindingVar.getClass();
        Class cls = binding.getClass();
        try {
            String methodName = "set" + bindingVar.first;
            Method method = cls.getDeclaredMethod(methodName, bindingVar.second.getClass());
            method.invoke(binding, bindingVar.second);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
    private void initPopupWindowIfNeeded() {
        if (mPopupWindow == null) {
            mPopupWindow = new FloatPopupWindow(mContext);
            mPopupWindow.setPopGlobalCallbacks(mPopGlobalCallbacks);
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
     * Builer for PopInn
     */
    public static class Builder {
        private Context context;
        private View anchorView;
        private List<PopGlobalCallback> popGlobalCallback;

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder anchorView(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        public Builder addGlobalCallback(PopGlobalCallback popGlobalCallback) {
            if(this.popGlobalCallback == null) {
                this.popGlobalCallback = new ArrayList<PopGlobalCallback>();
            }
            this.popGlobalCallback.add(popGlobalCallback);
            return this;
        }

        public PopInn build() {
            PopInn popInn = new PopInn(context, anchorView);
            popInn.setGlobalCallbacks(popGlobalCallback);
            return popInn;
        }
    }


}
