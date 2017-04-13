# popinn

A easy way to use popupwindow

PopupWindow经常用作引导、提示等功能，每个PopupWindow的业务逻辑不多，提供一套浮层框架统一管理PopupWindow，外部仅通过简单配置即可展示浮层，会比较方便。

展示方式主要通过Interface+Annotation。

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
PopInn popInn = new PopInn(MainApplication.getContext());
PopService popService = popInn.create(PopService.class);
```
Step3: 最后调用展示：
```
popService.showGeneralPop();
```

# Feature

- Interface + Annotation形式展现浮层
- 直接指定R.layout资源展示/指定contentView实例展示
- 浮层代理类支持（类似于Fragment的功能）
- 指定缩放比例的appScale支持
- 动画绑定
- 统一的dismiss处理；统一&分立的show/dismiss事件分发支持
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

    // 示例5：展示一个浮层，指定缩放值缩放
    @Layout(R.layout.scaled_pop)
    @AutoDismiss(dismissId = R.id.btn_close_2)
    PopHandle showScaledPop(@Scale float scale, @PopLayout.X int x, @PopLayout.Y int y, @PopLayout.W int width, 
                       @PopLayout.H int height);
    
    // 示例7：浮层代理类展示
    PopHandle showPopWithDelegate(@PopDelegate PopFragment fragment);

    // 示例8：DataBinding支持：绑定一个User类的实例
    @Layout(R.layout.pop_user)
    @AutoDismiss(dismissId = R.id.btn_close)
    PopHandle showDataBindingPop(@BindingVar("User") User user);
}
```

# Recipes

## 指定ContentView
指定ContentView有这样几种方法：

- 使用@Layout注解标注于方法上，指定layout布局资源
- 使用@ContentView注解，标注于参数前（未完成）

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
    - PopService的show方法，返回一个句柄PopHandle，用于外部操作，目前只有关闭功能和查询是否展示功能（是否还需要其他功能？）
    
## show/dissmiss的Callback支持
监听浮层的展示和消失，可以有两种方式，分别是：“总线式”和分立式。

- PopInn构建的时候可以设置一个全局的show/dismiss监听器。每次展现和关闭都给予一个回调。
- @Callback注解修饰参数，设置一个show/dismiss的回调（未完成）

```

public interface PopGlobalCallback {
    public void onPopShow(String popTag);
    public void onPopClosed(String popTag);
}

public interface PopCallback {
    public void onPopShow();
    public void onPopClosed();
}
```

## 程序缩放支持

- 使用@Scale注解修饰参数，传递float类型参数，可以对整个ContentView进行一定的缩放
（是否有兼容性问题？）
- 使用@Scale.Px和@Scale.Py注解，修饰缩放的锚点。默认是(0,0)点（未完成）

## 浮层代理类支持

使用@PopDelegate修饰参数，参数是继承于PopFragment的实例，可以使用代理类展示。
代理类只需实现onCreateView, onCreatePopLayoutParams, onDestroyView方法，即可展示出来。使用mHost的相关方法，操作PopupWindow本身。

```
public abstract class PopFragment {

    protected PopupWindowCallback mHost;

    public abstract View onCreateView(Context context, ViewGroup parentView);

    public abstract PopLayoutParams onCreatePopLayoutParams();

    public abstract void onDestroyView();

    final void setHost(PopupWindowCallback popupWindowCallback) {
        mHost = popupWindowCallback;
    }
}
```

## 动画绑定支持
通过@BindAnim注解标注在参数前，指定浮层的进入/退出动画

- @BindAnim("style"), 修饰一个R.style.xxx的id直接给浮层设置一个动画资源（支持enter/exit等）
- @BindAnim("enter")，修饰一个`List<Pair<Integer, Object>>`，修饰一个{View,动画}对的列表，指定进入时各个控件的动画（未完成）
- @BindAnim("enter")，修饰一个`List<Pair<Integer, Object>>`，修饰一个{View,动画}对的列表，指定退出时各个控件的动画（未完成）


## 数据绑定支持

数据绑定包括两种，简单绑定（单向）和DataBinding

- 简单绑定（未完成）
    - 使用@BindingText(R.id.xxx)修饰参数，可以为部分控件调用setText填充数据
    - 使用@BindingClick(R.id.xxx)修饰参数，可以为部分控件绑定onClick回调
    - 使用@BindingColor(resId = R.id.xxx, type = "back")，可以为部分控件的前景或背景设置颜色（或者是刷色？）
- DataBinding支持
    - 要求@Layout修饰一个DataBinding的资源
    - 使用@BindingVar("xxx")参数修饰绑定的对象，指明variable参数
