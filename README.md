# PowerfulLoadingView
加载动画视图，支持加载成功和加载失败的多状态动画效果，视图颜色可自定义

## 效果图
![](http://bmob-cdn-15184.b0.upaiyun.com/2017/12/28/35333a0140d50efc80fa36fb6b1717eb.gif)

## 可自定义的颜色
* app:loading_bar_color	加载条颜色
* app:bg_color 加载视图背景色
* app:tick_cross_color 动画中钩和叉的颜色

## xml配置
``` xml
<com.wang.powerfulloadingview.PowerfulLoadingView
    android:id="@+id/loading_view"
    app:loading_bar_color="@android:color/holo_red_dark"
    app:bg_color="@android:color/black"
    app:tick_cross_color="@android:color/white"
    android:layout_width="50dp"
    android:layout_height="50dp" />
```

## 使用
``` java
//显示加载动画
mLoadingView.startLoading();

//加载成功动画
mLoadingView.loadSucceed(AnimatorListener listener)

//加载失败动画
mLoadingView.loadFailed(AnimatorListener listener)

//清空所有动画，避免内存泄露
mLoadingView.clearAllAnimator()
```


