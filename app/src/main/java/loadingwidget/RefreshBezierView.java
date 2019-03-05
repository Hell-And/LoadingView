package loadingwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import com.example.lixiang.loadingview.R;

import util.MyUtil;

/**
 * Created by xiang on 2018/3/23.
 * 花椒直播下拉刷新
 */

public class RefreshBezierView extends View {
    private final static String TAG = "RefrashFourView";
    private Paint mPaint;
    private Path mPath;
    private Context mContext;
    //起始点1，起始点2，结束点1，结束点2，控制点
    private PointF start1, start2, end1, end2, control;
    //圆心坐标
    private PointF circlr1, circlr2;
    //两个圆心之间的距离
    private float distanceC;
    //最大距离
    private float distanceMax;
    //半径
    private float circlrRadius = 15f;
    private int beisaierColor;
    private int color1;
    private int color2;
    private int width, height;
    private boolean isStar = false;
    private long duration = 2000;
    private int alpha;
    private ValueAnimator valueAnimator;

    public RefreshBezierView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RefreshBezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public RefreshBezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        width = widthSize;
//        height = heightSize;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            circlr1.x = circlrRadius;
            circlr2.x = widthSize - circlrRadius;
            distanceMax = widthSize - 2 * circlrRadius;
        } else {
            width = (int) (circlrRadius * 7);
            circlr1.x = widthSize / 2 - 2.5f * circlrRadius;
            circlr2.x = widthSize / 2 + 2.5f * circlrRadius;
            //勾股定律
//            distanceMax = (float) Math.hypot(circlr2.x - circlr1.x, circlr2.y - circlr1.y);
            distanceMax = Math.abs(circlr2.x - circlr1.x);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (circlrRadius * 3) + getPaddingTop() + getPaddingBottom();
        }

        circlr1.y = height / 2;
        circlr2.y = height / 2;
        setMeasuredDimension(width, height);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RefreshBezierView);
        circlrRadius = a.getDimension(R.styleable.RefreshBezierView_radius, 10);//初始半径
        color1 = a.getColor(R.styleable.RefreshBezierView_leftColor, Color.parseColor("#05bfed"));
        color2 = a.getColor(R.styleable.RefreshBezierView_rightColor, Color.parseColor("#f7cb09"));
        beisaierColor = a.getColor(R.styleable.RefreshBezierView_bezier_color, Color.parseColor("#ff0000"));
        duration = a.getInteger(R.styleable.RefreshBezierView_bezier_duration, 1000);
        a.recycle();

        mPaint = new Paint();
        mPath = new Path();
        start1 = new PointF();
        start2 = new PointF();
        end1 = new PointF();
        end2 = new PointF();
        control = new PointF();
        circlr1 = new PointF();
        circlr2 = new PointF();

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(beisaierColor);
        mPaint.setAlpha(alpha);
        MyUtil.twoBeisaier(start1, start2, end1, end2, control,
                circlr1, circlr2, distanceC, circlrRadius, circlrRadius, mPaint, mPath, canvas);

        mPaint.setAlpha(255);
        //画圆
        mPaint.setColor(color1);
        canvas.drawCircle(circlr1.x, circlr1.y, circlrRadius, mPaint);
        mPaint.setColor(color2);
        canvas.drawCircle(circlr2.x, circlr2.y, circlrRadius, mPaint);

        if (!isStar) {
            isStar = true;
            startAnim();
        }
    }

    @SuppressLint("WrongConstant")
    private void startAnim() {
        valueAnimator = ValueAnimator.ofFloat(width / 2 - distanceMax / 2, width / 2 + distanceMax / 2);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复,来回滚动效果
        valueAnimator.setRepeatMode(Animation.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                circlr1.x = angle;
                circlr2.x = width - angle;

                //勾股定律
                distanceC = (float) Math.hypot(circlr2.x - circlr1.x, circlr2.y - circlr1.y);
                //透明度
                alpha = (int) ((1 - distanceC / distanceMax) * 255f);
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
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
