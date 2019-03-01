package loadingwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
 * Created by xiang on 2018/3/23.
 */

public class RefreshGradientView extends View {


    //第一个圆初始半径 即为最左边的圆球初始半径
    private float leftRadius = 10f;
    private float leftCirclrX;
    //默认圆球个数
    private int circleCount = 5;
    //圆球半径变化系数 圆球越多或初始半径越大，系数越大越平滑 但是不能超过1
    private float baseCoefficient = 0.85f;
    //第三个圆初始半径
    private float rightRadius;
    private long duration = 800;
    private Paint mpaint;
    private Context mContext;
    private int width;
    private int height;
    //圆球变化最大和最小值
    private float changeRadiosMax = leftRadius;
    private float changeRadiosMin = changeRadiosMax / circleCount;
    private boolean mIsRunning = true;
    private int deepColor, paleColor;
    private List<Circle> circles = new ArrayList<>();
    //圆球时时半径
    private float animRadius;
    //以最右边圆球为基准计算的半径
    private float rightReferenceRadius;
    //以最左边圆球为基准计算的半径
    private float leftReferenceRadius;
    //测量模式
    private int mode;
    private ValueAnimator valueAnimator;

    public RefreshGradientView(Context context) {
        super(context);
        this.mContext = context;
    }


    public RefreshGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public RefreshGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RefreshGradientView);
        leftRadius = a.getDimension(R.styleable.RefreshGradientView_initRadius, 10);//初始半径
        deepColor = a.getColor(R.styleable.RefreshGradientView_deepColor, Color.parseColor("#FF030BA1"));
        paleColor = a.getColor(R.styleable.RefreshGradientView_paleColor, Color.parseColor("#FF8E9DF4"));
        duration = a.getInteger(R.styleable.RefreshGradientView_gradient_duration, 600);
        circleCount = a.getInteger(R.styleable.RefreshGradientView_ball_counts, 3);//圆球个数，默认三个
        baseCoefficient = a.getFloat(R.styleable.RefreshGradientView_baseCoefficient, 0.85f);
        a.recycle();

        //取值越界判断
        duration = duration < 3 ? 3 : duration;
        baseCoefficient = (baseCoefficient < 0 || baseCoefficient > 1) ? 0.85f : baseCoefficient;

        changeRadiosMax = leftRadius;
        changeRadiosMin = changeRadiosMax / circleCount;

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

        width = widthSize;
//        height = heightSize;
        mode = widthMode;
        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//            leftCirclrX = -widthSize / 2;
        } else {
            width = (int) (leftRadius * 2 * (circleCount + circleCount - 1));
//            int desired = getPaddingLeft() + getPaddingRight();
//            width = desired <= widthSize ? desired : widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (leftRadius * 3 + getPaddingTop() + getPaddingBottom());
        }
        if (circles.size() == 0) {
//            initview();
        }
        setMeasuredDimension(width, height);
    }

    private void initview() {
//        mpaint.setColor(Color.parseColor("#eab3a7"));
        circles.clear();
        for (int i = 0; i < circleCount; i++) {
            //计算圆球圆心位置
            if (mode == MeasureSpec.EXACTLY) {
                //固定宽度时以控件宽度为准
                leftCirclrX = leftRadius + i * (width - leftRadius * 2) / (circleCount - 1);
            } else {
                //wrap_content时 以圆球半径计算 每个圆球之间各一个圆球的间隙
                leftCirclrX = width / 2 - (circleCount * 2 - 1) * leftRadius + leftRadius + leftRadius * 4 * i;
//                leftCirclrX = leftRadius + leftRadius * 2 * i;
            }
            circles.add(new Circle(leftCirclrX, height / 2));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initview();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心设为原点(0,0)
//        canvas.translate(width / 2, height / 2);
        for (Circle circle : circles) {
            mpaint.setColor(circle.getColor());
            //画圆
            canvas.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), mpaint);
        }

        if (mIsRunning) {
            starAnim();
            mIsRunning = false;
        }

    }

    @SuppressLint("WrongConstant")
    private void starAnim() {
        valueAnimator = ValueAnimator.ofFloat(0, changeRadiosMax - changeRadiosMin);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复,来回动画效果
        valueAnimator.setRepeatMode(Animation.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                leftRadius = changeRadiosMax - angle;
                rightRadius = changeRadiosMin + angle;
                //第一个球和最后一个球
//                circles.get(0).setRadius(leftRadius);
//                circles.get(0).setColor(ColorUtil.getColorChanges(deepColor, paleColor, leftRadius / changeRadiosMax));
//                circles.get(circles.size() - 1).setRadius(rightRadius);
//                circles.get(circles.size() - 1).setColor(ColorUtil.getColorChanges(deepColor, paleColor, rightRadius / changeRadiosMax));
                for (int i = 0; i < circles.size(); i++) {
                    leftReferenceRadius = (float) (leftRadius * Math.pow(baseCoefficient, i));
                    rightReferenceRadius = (float) (rightRadius * Math.pow(baseCoefficient, (circles.size() - 1 - i)));

                    //计算圆球时时半径 多次试验得出的公式
                    //当最左 大于 最右圆球的半径时 靠左边的圆球半径应该以最左边的半径为基准变化
                    if (leftRadius > rightRadius) {
                        //以最左边圆球半径按系数计算出来的半径 不能小于 最右边的圆球半径乘以系数，否则就要以最右边圆球半径按系数计算
                        if (leftReferenceRadius >= rightRadius * baseCoefficient) {
                            animRadius = leftReferenceRadius;
                        } else {
                            animRadius = rightReferenceRadius;
                        }
                    } else if (leftRadius <= rightRadius) {
                        //当最左 小于 最右圆球的半径时 靠左边的圆球半径应该以最右边的半径为基准变化
                        //以最右边圆球半径按系数计算出来的半径 不能小于 最左边的圆球半径乘以系数，否则就要以最左边圆球半径按系数计算
                        if (rightReferenceRadius >= leftRadius * baseCoefficient) {
                            animRadius = rightReferenceRadius;
                        } else {
                            animRadius = leftReferenceRadius;
                        }
                    }
                    circles.get(i).setRadius(animRadius);
                    circles.get(i).setColor(MyUtil.getColorChanges(deepColor, paleColor, animRadius / changeRadiosMax));
                }
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

    public synchronized void addBall() {
        circleCount++;
        initview();
        postInvalidate();
    }

    public synchronized void delBall() {
        if (circleCount == 3) {
            return;
        }
        circleCount--;
        initview();
        postInvalidate();
    }

    public void start() {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                if (valueAnimator != null && !valueAnimator.isRunning()) {
                    valueAnimator.start();
                }
//                animate().alpha(1).setDuration(200);
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
