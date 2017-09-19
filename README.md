# Introduction

A easy way to use popupwindow

Step1: 首先构建一个展示浮层的接口，内部是业务相关的展示方法，一个方法对应一个浮层：
```
public interface PopService {
    // 示例1：展示一个全屏浮层，3s自动关闭
    @Layout(R.layout.general_pop)
    @AutoDismiss(delay = 3000)
    PopHandle showGeneralPop();
}
```
Step2: 然后构建PopInn实例，生成代理类：
```
PopInn popInn = new PopInn();
PopService popService = popInn.create(PopService.class);
```
Step3: 调用展示：
```
popService.attach(context).with(anchorView).showGeneralPop();
```

Step4: Anchor视图生命周期隐藏时，释放资源

```
popService.closePopupWindow(); // 任意位置关闭浮层
popService.detatch(); // anchor视图onStop/onDestroy时detach
```

# Feature

- Interface + Annotation形式展现浮层
- 直接指定R.layout资源展示/指定contentView实例展示UI内容
- 更方便地指定浮层位置
- 封装浮层代理类（类似Fragment）
- 自动夜间模式支持
- 指定缩放比例的appScale支持
- 动画绑定
- 统一&分立的show/dismiss事件分发; 统一/分立的浮层状态查询
- 数据绑定

# Example

```
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
    PopHandle showPopWithDelegate(@PopDelegate PopFragment fragment);

    // 示例8：DataBinding支持：绑定一个User类的实例
    @Layout(R.layout.pop_user)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showDataBindingPop(@BindingVar("User") User user);
}
```

# Recipes

首先讲一下整个浮层框架的规则：其实类似于Activity和Fragment的关系，全局的PopupWindow只有一个实例，每次展示时只是填充不同的视图。另外，每次新展示一个浮层的时候，需要把前一个浮层关闭，当然，一个关闭的Callback会被传递出来。

我们提供一个全局的closePopupWindow方法，即:不管当前展示哪个浮层，我们一律关闭。

如果我们只想关闭某一个浮层，我们需要在展示时保存它的“关闭句柄”，用这个句柄关闭浮层。

## 指定ContentView
指定ContentView有这样几种方法：

- 使用@Layout注解标注于方法上，指定layout布局资源
- 使用@ContentView注解，标注于参数前

## 指定布局位置
我们通过PopLayout接口里面的几个注解指定浮层的布局位置。

- @PopLayout.X指定x
- @PopLayout.Y指定y
- @PopLayout.W指定w
- @PopLayout.H指定h
- @PopLayout.G指定Gravity

如果不标注X/Y/W/H，那么浮层的大小将依据提供的R.layout资源的根视图的layout_width/layout_height的设定。即：根视图是wrap_content的，那么浮层也会是wrap_content；根视图是match_parent，浮层也会是match_parent。配合gravity，用起来会更方便一点。

## Dismiss支持
关闭指定的浮层，有自动关闭和手动关闭两种方法：

- @AutoDismiss进行自动关闭
    - 使用@AutoDismiss(delay=10)注解，指定延迟多长时间自动关闭
    - 使用@AutoDismiss(dismissId=R.id.xx)注解，指定关闭浮层的View的资源Id
- 主动关闭
    - PopService的show方法，返回一个句柄PopHandle，用于外部操作，目前只有关闭功能和查询是否展示功能

## show/dissmiss的Callback支持
监听浮层的展示和消失，可以有两种方式，分别是：“总线式”和分立式。

- PopInn构建的时候可以设置一个全局的show/dismiss监听器。每次展现和关闭都给予一个回调。
- @Callback注解修饰参数，设置一个show/dismiss的回调（未完成）

```

public interface PopGlobalCallback {
    public void onPopShow(Object popTag);
    public void onPopClosed(Object popTag);
}

public interface PopCallback {
    public void onPopShow();
    public void onPopClosed();
}
```

## 程序缩放支持

- 使用@Scale注解修饰参数，传递float类型参数，可以对整个ContentView进行一定的缩放
（是否有兼容性问题？）
- 使用@Scale.Px和@Scale.Py注解，修饰缩放的锚点。默认是(0,0)点

## 自动夜间模式支持
自动夜间模式支持需要两个步骤：一是指定用何种方法实现夜间模式展现；二是传递一个布尔值表示当前是夜间模式还是日间模式。

- 使用@NightMode标注于方法前，注明该浮层支持夜间模式并注明实现方式
    - Mode.NONE: 不支持夜间模式
    - Mode.COVER: 以盖一个蒙层的形式展示夜间模式
    - Mode.COLOR_FILTER: 以设定ColorFilter的形式展示夜间模式
- 使用@NightSwitch标注于一个boolean型参数前，注明当前是日间模式还是夜间模式

## 浮层代理类支持

使用@PopDelegate修饰参数，参数是继承于PopFragment的实例，可以使用代理类展示。
代理类的生命周期包括三个：onCreateView, onCreatePopLayoutParams, onDestroyView，实现生命周期方法，即可展示出浮层。使用mHost的相关方法，操作PopupWindow本身。

```
/**
 * Fragment of a pop which controls its lifecycle
 *
 */
public abstract class PopFragment {

    protected PopupWindowCallback mHost;

    /**
     * if current PopFragment isShowing, true between onCreateView and onDestroyView
     */
    private boolean mIsShowing;

    /**
     * Called to have the PopFragment instantiate its user interface view.
     * @param context instance of the ui context
     * @param parentView Optional view to be the parent of the generated hierarchy
     * @return The root View of the inflated hierarchy
     */
    public abstract View onCreateView(Context context, ViewGroup parentView);

    /**
     * Called to have the popLayoutParams, which is used to locate the fragment
     * @return instance of the PopLayoutParams
     */
    public abstract PopLayoutParams onCreatePopLayoutParams();

    /**
     * Called When the popupWindow is dismissed
     */
    public abstract void onDestroyView();

    /**
     * Return true if the popupWindow is showing the fragment
     */
    public boolean isShowing() {
        return mIsShowing && (mHost != null && mHost.isShowing());
    }

    final void setHost(PopupWindowCallback popupWindowCallback) {
        mHost = popupWindowCallback;
    }
}
```

## 动画绑定支持
通过@BindAnim注解标注在参数前，指定浮层的进入/退出动画

- @BindAnim("style"), 修饰一个R.style.xxx的id直接给浮层设置一个动画资源（支持enter/exit等）


## 数据绑定支持

数据绑定包括两种，简单绑定（单向）和DataBinding

- 简单绑定
    - 使用@BindingText(R.id.xxx)修饰参数，可以为部分控件调用setText填充数据
    - 使用@BindingClick(R.id.xxx)修饰参数，可以为部分控件绑定onClick回调
- DataBinding支持
    - 要求@Layout修饰一个DataBinding的资源
    - 使用@BindingVar("xxx")参数修饰绑定的对象，指明variable参数