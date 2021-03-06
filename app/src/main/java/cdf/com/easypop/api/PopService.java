/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import com.citrus.popinn.PopFragment;
import com.citrus.popinn.PopHandle;
import com.citrus.popinn.PopupCallback;
import com.citrus.popinn.anno.AutoDismiss;
import com.citrus.popinn.anno.BindAnim;
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

import android.view.View;
import cdf.com.easypop.R;
import cdf.com.easypop.model.MyConfig;
import cdf.com.easypop.model.User;

/**
 * Created by cdf on 17/3/11.
 */
public interface PopService {

    // 示例1：展现一个全屏浮层，3s自动消失
    @Layout(R.layout.general_pop)
    @AutoDismiss(delay = 3000)
    PopHandle showGeneralPop();

    // 示例2：指定位置、宽度、高度展示浮层，点击指定id的控件关闭
    @Layout(R.layout.general_pop_2)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showGeneralPop2(@PopLayout.X int x, @PopLayout.Y int y, @PopLayout.W int width, 
                         @PopLayout.H int height);

    // 示例3：展示一个浮层，指定进入/退出动画
    @Layout(R.layout.animated_pop)
    @BindAnim(animStyle = R.style.pop_anim)
    @AutoDismiss(delay = 3000)
    PopHandle showAnimatedPop();

    // 示例4：展示一个浮层，采用盖一个蒙层方式实现夜间模式
    @Layout(R.layout.night_mode_pop)
    @NightMode(NightMode.Mode.COVER)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showNightModePop(@NightSwitch boolean isNight);

    // 示例5：展示一个浮层，指定缩放值缩放
    @Layout(R.layout.scaled_pop)
    @AutoDismiss(dismissId = R.id.btn_close_2)
    PopHandle showScaledPop(@Scale float scale, @PopLayout.X int x, @PopLayout.Y int y, @PopLayout.W int width,
                            @PopLayout.H int height);

    // 示例6：展示一个浮层，指定使用ColorFilter展示夜间模式
    @Layout(R.layout.night_mode_pop)
    @NightMode(NightMode.Mode.COLOR_FILTER)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showNightModePop2(@NightSwitch boolean isNight);
    
    // 示例7：浮层代理类展示
    PopHandle showPopWithDelegate(@PopDelegate PopFragment fragment, @Tag Object tag);

    // 示例8：DataBinding支持：绑定一个User类的实例
    @Layout(R.layout.pop_user)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showDataBindingPop(@BindingVar("User") User user);

    @Layout(R.layout.night_mode_pop)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showPopWithGravity(@PopLayout.G int gravity);

    @Layout(R.layout.night_mode_pop)
    PopHandle showPopWithoutAutoDismiss(@PopLayout.G int gravity);

    @Layout(R.layout.click_binding)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showClickBindingPop(@BindingClick View.OnClickListener listener);
    
    @AutoDismiss(dismissId = R.id.btn_close)
    @Layout(R.layout.check_box_binding)
    void showCheckboxDataBinding(@BindingVar("Config") MyConfig config,
                                 @PopLayout.G int gravity, @Callback PopupCallback callback);
}
