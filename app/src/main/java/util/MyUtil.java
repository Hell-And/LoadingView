package util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/**
 * Created by xiang
 * on 2019/2/27 14:08
 */
public class MyUtil {
    public static int getColorChanges(int startcolor, int endcolor, float angle) {
        int R, G, B;
        // 颜色的渐变，应该把分别获取对应的三基色，然后分别进行求差值；这样颜色渐变效果最佳
        R = (int) (Color.red(startcolor) + (Color.red(endcolor) - Color.red(startcolor)) * angle);
        G = (int) (Color.green(startcolor) + (Color.green(endcolor) - Color.green(startcolor)) * angle);
        B = (int) (Color.blue(startcolor) + (Color.blue(endcolor) - Color.blue(startcolor)) * angle);

        return Color.rgb(R, G, B);
    }
    //二阶贝塞尔曲线
    public static void twoBeisaier(PointF start1, PointF start2, PointF end1,
                                   PointF end2, PointF control, PointF circlr1, PointF circlr2,
                                   float distanceC, float radius1, float radius2, Paint paint, Path path,
                                   Canvas canvas){
        //计算贝塞尔曲线 起点 终点 控制点
        //控制点坐标
        control.x = (circlr2.x + circlr1.x) / 2;
        control.y = (circlr2.y + circlr1.y) / 2;
        //获取角度余弦值正弦值
        float sin = (circlr2.y - circlr1.y) / distanceC;
        float cos = (circlr2.x - circlr1.x) / distanceC;
        //起点一
        start1.x = circlr1.x - radius1 * sin;
        start1.y = circlr1.y + radius1 * cos;

        //终点一
        end1.x = circlr2.x - radius2 * sin;
        end1.y = circlr1.y + radius2 * cos;

        //起点二
        start2.x = circlr2.x + radius2 * sin;
        start2.y = circlr1.y - radius2 * cos;

        //终点二
        end2.x = circlr1.x + radius1 * sin;
        end2.y = circlr1.y - radius1 * cos;


        //绘制贝塞尔曲线
        path.reset();
        //起点坐标(定义初始位置)
        path.moveTo(start1.x, start1.y);
        //参数：控制点坐标，终点坐标,,,,,第一条贝塞尔曲线
        path.quadTo(control.x, control.y, end1.x, end1.y);
        //画一条直线，移动到新的贝塞尔曲线起点
        path.lineTo(start2.x, start2.y);
        //第二条贝塞尔曲线
        path.quadTo(control.x, control.y, end2.x, end2.y);
        //关闭
        path.close();
        //画
        canvas.drawPath(path, paint);
    }
}
