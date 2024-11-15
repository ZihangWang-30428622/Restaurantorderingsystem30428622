package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class CircleView extends View {
    private SparseArray<Circle> circleArray = new SparseArray<>();
    private Paint paint;

    // Circle 类用于存储每个触摸点的坐标
    private static class Circle {
        float x;
        float y;
    }

    // CircleView 构造函数
    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制所有触摸点
        int numCircles = circleArray.size();
        for (int i = 0; i < numCircles; ++i) {
            Circle circle = circleArray.valueAt(i);
            if (circle != null) {
                canvas.drawCircle(circle.x, circle.y, 50, paint); // 绘制半径为50的圆
            }
        }
    }

    public void updateCircleArray(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                Circle circle = new Circle();
                circle.x = event.getX(pointerIndex);
                circle.y = event.getY(pointerIndex);
                circleArray.put(pointerId, circle);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    Circle movedCircle = circleArray.get(event.getPointerId(i));
                    if (movedCircle != null) {
                        movedCircle.x = event.getX(i);
                        movedCircle.y = event.getY(i);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                circleArray.remove(pointerId);
                break;
        }

        // 触发重绘，显示更新的触摸点
        invalidate();
    }
}
