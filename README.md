# LoadingView
ValueAnimator基础使用，模仿花椒直播刷新控件，自定义loading效果
# 效果
![](https://github.com/LoverAnd/loadingView/blob/master/refresh.gif)

# ValueAnimator 基本用法

```java
//取值器 从start到end不断变化
ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        //匀速动画
        valueAnimator.setInterpolator(new LinearInterpolator());
        //时间
        valueAnimator.setDuration(duration);
        //无线循环
        valueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复,来回动画效果
        valueAnimator.setRepeatMode(Animation.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                // angle 不断变化的值 根据这个值做处理 可以实现各种动画
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        valueAnimator.start();//开始取值
