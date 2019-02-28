package loadingwidget;

/**
 * Created by xiang
 * on 2019/2/27 18:22
 */
public class Circle {
    private float x;
    private float y;
    private int color;
    private float radius;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Circle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Circle(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Circle(float x, float y, float radius, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.radius = radius;
    }
}
