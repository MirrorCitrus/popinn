/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

import cdf.com.easypop.R;
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
public interface PopService {
    
    @Layout(R.layout.general_pop)
    @LayoutDimension(LayoutDimension.LayoutDimensionMode.MATCH_PARENT)
    @AutoDismiss(delay = 3000)
    void showGeneralPop();

    @Layout(R.layout.general_pop_2)
    @AutoDismiss(dismissId = R.id.btn_close)
    void showGeneralPop2(@LocX int x, @LocY int y, @PopW int width, @PopH int height);

    @Layout(R.layout.animated_pop)
    @BindAnim(animStyle = R.style.pop_anim)
    @LayoutDimension(LayoutDimension.LayoutDimensionMode.WRAP_CONTENT)
    @AutoDismiss(delay = 3000)
    void showAnimatedPop();

    @Layout(R.layout.night_mode_pop)
    @NightMode(NightMode.Mode.COVER)
    @AutoDismiss(dismissId = R.id.btn_close)
    @LayoutDimension(LayoutDimension.LayoutDimensionMode.WRAP_CONTENT)
    void showNightModePop(@NightSwitch boolean isNight);

    @Layout(R.layout.scaled_pop)
    @AutoDismiss(dismissId = R.id.btn_close_2)
    void showScaledPop(@Scale float scale, @LocX int x, @LocY int y, @PopW int width, @PopH int height);

    @Layout(R.layout.night_mode_pop)
    @NightMode(NightMode.Mode.COLOR_FILTER)
    @AutoDismiss(dismissId = R.id.btn_close)
    @LayoutDimension(LayoutDimension.LayoutDimensionMode.WRAP_CONTENT)
    void showNightModePop2(@NightSwitch boolean isNight);
}
