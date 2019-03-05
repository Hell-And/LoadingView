package loadingwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;


import com.example.lixiang.loadingview.R;

import java.util.ArrayList;
import java.util.List;

import util.MyUtil;

/**
 * Created by xiang on 2018/3/7.
 */

public class RefreshCircleView extends View {
    //静态圆圆心，动态圆圆心，大圆圆心
    private PointF staticCircle, bigCircle;
    //大圆默认半径，
    private float bidCircleRadiosdefult = 30f;
    //大圆半径，
    private float bidCircleRadios = bidCircleRadiosdefult;
    //第一个动态圆圆心
    private float mCircleRadios = bidCircleRadios / 5;
    /**
     * 静态圆变化半径的最大比率
     */
    private float mMaxStaticCircleRadiusScaleRate = 0.4f;
    /**
     * 静态圆个数
     */
    private int mCircleCount = 8;
    //静态圆圆心集合
    private List<Circle> staticCircleList = new ArrayList<>();
    private Paint dragCirclePaint = new Paint();
    /*
     * 开始角度到结束角度
     * 应该是两圈 endDeg - startDeg =720
     * */
    private float startDeg = -90f;
    private float endDeg = 720f + startDeg;
    private long duration = 6000l;
    private int startColor;
    private int endColor;
    private int width, height;
    private long playtime;

    public RefreshCircleView(Context context) {
        super(context);
//        dragCirclePaint.setColor(context.getResources().getColor(R.color.red));
    }

    public RefreshCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        dragCirclePaint.setColor(context.getResources().getColor(R.color.red));
        initview(attrs);
    }

    private void initview(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RefreshCircleView);
        mCircleRadios = a.getDimension(R.styleable.RefreshCircleView_circleRadius, (int) mCircleRadios);//圆半径
        startColor = a.getColor(R.styleable.RefreshCircleView_startColor, Color.parseColor("#ff0000"));
        endColor = a.getColor(R.styleable.RefreshCircleView_endColor, Color.parseColor("#f49b9b"));
        duration = a.getInteger(R.styleable.RefreshCircleView_circle_duration, 3000);
        mCircleCount = a.getInteger(R.styleable.RefreshCircleView_circle_counts, 8);
        a.recycle();

        /* 画笔 */
        dragCirclePaint.setStyle(Paint.Style.FILL);
        dragCirclePaint.setAntiAlias(true);
        staticCircle = new PointF(0, 0);
        bigCircle = new PointF(0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //三角形底部边长 默认长度是五个圆的直径长
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            bidCircleRadios = width / 2 - 2 * mCircleRadios;
        } else {
            //大圆半径加上静态圆半径（静态圆半径会变化，这里考虑变化到最大值）,乘以2得出的是控件的尺寸
            width = height = (int) (2 * (bidCircleRadios + mCircleRadios * (1 + mMaxStaticCircleRadiusScaleRate)));
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (2 * (bidCircleRadios + mCircleRadios * (1 + mMaxStaticCircleRadiusScaleRate)));
        }
        //大圆圆心
        bigCircle.x = width / 2;
        bigCircle.y = height / 2;

        //静态圆圆心,坐标可以根据大圆圆心坐标的正玄值余弦值计算,cos(余弦值)toRadians(将角度转换为弧度)
        for (int i = mCircleCount - 1; i >= 0; i--) {
//            staticCircle.x = (float) (bigCircle.x + bidCircleRadios * Math.cos(Math.toRadians(-90)));
//            staticCircle.y = (float) (bigCircle.y + bidCircleRadios * Math.sin(Math.toRadians(-90)));
            Circle circle = new Circle();
            circle.setColor(MyUtil.getColorChanges(startColor, endColor, (float) ((mCircleCount - i + 0.0) / mCircleCount)));
            staticCircleList.add(circle);
        }


        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isrotate) {
//            canvas.rotate(-90f, bigCircle.x, bigCircle.y);
            startAnim();
            isrotate = false;
        }
        for (int i = mCircleCount - 1; i >= 0; i--) {
            dragCirclePaint.setColor(staticCircleList.get(i).getColor());
            canvas.drawCircle(staticCircleList.get(i).getX(), staticCircleList.get(i).getY(), staticCircleList.get(i).getRadius(), dragCirclePaint);
        }
    }

    private float speedradiu = 0;
    private boolean isrotate = true;

    //使用ValueAnimator取值器动态获取动态圆圆心坐标
    private void startAnim() {
        /* 角度 */
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startDeg, endDeg);
        valueAnimator.setDuration(duration);
        //以常量速率改变，设置后匀速动画
        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setRepeatCount(5);
        //无限循环
        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();


//                playtime = animation.getCurrentPlayTime();
                //静态圆圆心,坐标可以根据大圆圆心坐标的正玄值余弦值计算,cos(余弦值)toRadians(将角度转换为弧度) Math.cos方法参数是弧度
//                staticCircleList.clear();

                for (int i = 0; i < mCircleCount; i++) {
                    //以第一个大圆走的路程为基准判断 分为两圈两个阶段 第一圈速度递减(1 - i / mCircleCount) 第二圈速度递增 (1 + i / mCircleCount)
                    if (angle <= (endDeg + startDeg) / 2) {
                        staticCircle.x = (float) (bigCircle.x + bidCircleRadios * Math.cos(Math.toRadians(
                                startDeg   //开始的角度
                                        + (angle - startDeg) *   //以第一个圆的路程为基准 这就是第一个圆第一阶段走过的路程
                                        (mCircleCount - i) / mCircleCount))); //速度递减(1 - i / mCircleCount)
                        staticCircle.y = (float) (bigCircle.y + bidCircleRadios * Math.sin(Math.toRadians(startDeg + (angle - startDeg) * (mCircleCount - i) / mCircleCount)));
                    } else {
                        staticCircle.x = (float) (bigCircle.x + bidCircleRadios * Math.cos(Math.toRadians(
                                (360f - 360f / mCircleCount * i + startDeg)  //上一阶段最后的角度 就是这个阶段开始的角度
                                        + (angle - (endDeg + startDeg) / 2) * //以第一个圆的路程为基准 这就是第一个圆第二阶段走过的路程
                                        ((mCircleCount + i + 0.0f) / mCircleCount))));//速度递增(1 + i / mCircleCount)  加0.0f是为了避免精度丢失
                        staticCircle.y = (float) (bigCircle.y + bidCircleRadios * Math.sin(Math.toRadians((360f - 360f / mCircleCount * i + startDeg) + (angle - (endDeg + startDeg) / 2) * ((mCircleCount + i + 0.0f) / mCircleCount))));

//                          这种方式在小米mix2（8.0 API26） 中有严重误差 不知原因
//                        staticCircle.x = (float) (bigCircle.x + bidCircleRadios * Math.cos(Math.toRadians((playtime - duration / 2) * ((360f + 360f / mCircleCount * i) / (duration / 2)) + (360f - 360f / mCircleCount * i + startDeg))));
//                        staticCircle.y = (float) (bigCircle.y + bidCircleRadios * Math.sin(Math.toRadians((playtime - duration / 2) * ((360f + 360f / mCircleCount * i) / (duration / 2)) + (360f - 360f / mCircleCount * i + startDeg))));

                    }

                    //为了避免最后一个圆太小 这里做了一下处理
                    float radius = mCircleRadios * (mCircleCount + 1 - i) / mCircleCount + 1;
                    staticCircleList.get(i).setX(staticCircle.x);
                    staticCircleList.get(i).setY(staticCircle.y);
                    staticCircleList.get(i).setRadius(radius);
                }
                invalidate();
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
//                playtime = 0;
            }
        });
    }
}
