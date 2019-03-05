package loadingwidget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import com.example.lixiang.loadingview.R;

import java.util.ArrayList;
import java.util.List;

import util.MyUtil;

/**
 * Created by xiang on 2018/3/14.
 */
//模仿花椒直播数据加载效果
public class RefreshTriangleView extends View {


    //三个圆
    private Paint mpaint;
    private int circleCount = 3;
    //圆半径
    private float circleRadius;
    //三角形角度
    private double triangleradios;

    //三角形底部边长 默认长度是五个圆的直径长
    private float trianglength;
    //第二 三 个圆动画的距离
    private float motionLength;
    //第一个圆最高处
    private float topCircle = (float) (motionLength / (Math.cos(Math.toRadians(triangleradios))));
    //保留上一次的取值
    private float lastAngle;
    //动画时间
    private long duration;
    private int width, height;
    //运动圆集合
    private List<Circle> circles = new ArrayList<>();
    private boolean mIsRunning = true;
    private Context mContext;
    private ValueAnimator valueAnimator;
    private Circle getCircle1, getCircle2, getCircle3;
    //默认基础色为深红 到浅红
    private int baseDeepColor;
    private int baseMidColor;
    private int basePaleColor;
    private List<Integer> colors = new ArrayList<>();

    public RefreshTriangleView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RefreshTriangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public RefreshTriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RefreshTriangleView);
        triangleradios = a.getInteger(R.styleable.RefreshTriangleView_triangleradios, 40);//三角形角度
        circleRadius = a.getDimension(R.styleable.RefreshTriangleView_circleRadius, 10);//圆半径
        baseDeepColor = a.getColor(R.styleable.RefreshTriangleView_baseDeepColor, Color.parseColor("#ff0000"));
        basePaleColor = a.getColor(R.styleable.RefreshTriangleView_basePaleColor, Color.parseColor("#f4dcde"));
        duration = a.getInteger(R.styleable.RefreshTriangleView_duration, 500);
        a.recycle();

        mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        width = widthSize;
//        height = heightSize;


        //三角形底部边长 默认长度是五个圆的直径长
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            trianglength = width - circleRadius * 2;
        } else {
            trianglength = circleRadius * 10f;
            width = (int) (trianglength + circleRadius * 2);
//            int desired = getPaddingLeft() + getPaddingRight();
//            width = desired <= widthSize ? desired : widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (trianglength * Math.sin(Math.toRadians(triangleradios)) + getPaddingBottom() + getPaddingTop());
        }
        if (circles.size() == 0) {
            initview();
        }
        setMeasuredDimension(width, height);
    }

    private void initview() {

        //第二 三 个圆动画的距离
        motionLength = trianglength / 2;
        //第一个圆最高处
        topCircle = (float) (motionLength / (Math.cos(Math.toRadians(triangleradios))));
        baseMidColor = MyUtil.getColorChanges(baseDeepColor, basePaleColor, 0.5f);
        colors.add(baseDeepColor);
        colors.add(baseMidColor);
        colors.add(basePaleColor);

        for (int i = 0; i < circleCount; i++) {
            circles.add(new Circle(width / 2 - motionLength + motionLength * i, height - circleRadius, colors.get(i)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < circles.size(); i++) {

            mpaint.setColor(circles.get(i).getColor());
            //画圆
            canvas.drawCircle(circles.get(i).getX(), circles.get(i).getY(), circleRadius, mpaint);
        }

        if (mIsRunning) {
            statanim();
            mIsRunning = false;
        }

    }


    //使用ValueAnimator取值器动态获取动态圆圆心坐标

    private void statanim() {

        /* 角度 */
        valueAnimator = ValueAnimator.ofFloat(0, motionLength);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复,来回滚动效果
//        valueAnimator.setRepeatMode(Animation.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                //第二三个圆一直做 从左往右的动画
                if (circles.size() == 0) {
                    return;
                }
                getCircle2 = circles.get(1);
                getCircle2.setX(width / 2 - angle);
//                getCircle2.setY(height / 2);
                getCircle2.setColor(MyUtil.getColorChanges(baseMidColor, baseDeepColor, angle / motionLength));

                getCircle3 = circles.get(2);
                getCircle3.setX(width / 2 - angle + motionLength);
//                getCircle3.setY(height / 2);
                getCircle3.setColor(MyUtil.getColorChanges(basePaleColor, baseMidColor, angle / motionLength));


                //第一个圆一直做 从左到顶部再到最右的动画
                if (angle <= motionLength / 2) {
                    //第一阶段 (0 - motionLength / 2 => 0 - topCircle )
                    angle = angle / (motionLength / 2) * topCircle;
                } else {
                    //第二阶段 (topCircle - 0 => motionLength / 2 - motionLength )
                    angle = topCircle - (angle - motionLength / 2) / (motionLength / 2) * topCircle;
                }
                //利用正弦值余弦值求坐标
                float y = (float) (angle * Math.sin(Math.toRadians(triangleradios)));
                float x = (float) (angle * Math.cos(Math.toRadians(triangleradios)));

                getCircle1 = circles.get(0);
                if (angle - lastAngle < 0) {
                    getCircle1.setX(width / 2 + motionLength - x);
                    getCircle1.setY(height - circleRadius - y);
                    getCircle1.setColor(MyUtil.getColorChanges(baseMidColor, basePaleColor, 1 - angle / topCircle));
                } else {
                    getCircle1.setX(width / 2 - motionLength + x);
                    getCircle1.setY(height - circleRadius - y);
                    getCircle1.setColor(MyUtil.getColorChanges(baseDeepColor, baseMidColor, angle / topCircle));
                }
                lastAngle = angle;
                invalidate();
            }
        });

      /*  valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });*/
        valueAnimator.start();
    }

    public void stop() {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.end();
                }
                animate().alpha(0).setDuration(300);
            }
        });

    }

    public void start() {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                if (valueAnimator != null && !valueAnimator.isRunning()) {
                    valueAnimator.start();
                }
                animate().alpha(1).setDuration(300);
            }
        });

    }

    //创建Looper，否则报错
    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

}
