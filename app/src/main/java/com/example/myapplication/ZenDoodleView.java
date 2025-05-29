package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZenDoodleView extends View {

    private Path path;
    private Paint paint;
    private float lastX, lastY;
    private boolean isDrawing = false;
    private int score = 0;

    private static final float MAX_GAP_DISTANCE = 40f;

    private ScoreUpdateListener scoreUpdateListener;

    public ZenDoodleView(Context context) {
        super(context);
        init();
    }

    public ZenDoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(0xFF3F51B5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                lastX = x;
                lastY = y;
                isDrawing = true;
                score = 0;
                notifyScore();
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!isDrawing) return false;

                float dx = Math.abs(x - lastX);
                float dy = Math.abs(y - lastY);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > MAX_GAP_DISTANCE) {
                    isDrawing = false;
                    notifyScore();
                    return false;
                }

                path.lineTo(x, y);
                lastX = x;
                lastY = y;

                score++;
                notifyScore();

                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDrawing = false;
                notifyScore();
                return true;
        }

        return super.onTouchEvent(event);
    }

    public void reset() {
        path.reset();
        score = 0;
        isDrawing = false;
        notifyScore();
        invalidate();
    }

    private void notifyScore() {
        if (scoreUpdateListener != null) {
            scoreUpdateListener.onScoreUpdated(score, isDrawing);
        }
    }

    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreUpdateListener = listener;
    }

    public interface ScoreUpdateListener {
        void onScoreUpdated(int score, boolean isDrawing);
    }
}


