package com.fta.testfloatingservice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class MyProgressView extends View {
    private int width = 200;
    private int height = 200;
    private Paint circlePaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Canvas bitmapCanvas;
    private Bitmap bitmap;
    private Path path = new Path();
    private int progress = 50;
    private int max = 100;
    private GestureDetector gestureDetector;
    private int currentProgress = 0;

    private int count = 50;

    private boolean isSingleTap = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public MyProgressView(Context context) {
        this(context, null);
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.argb(0xff, 0x3a, 0x8c, 0x6c));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.argb(0xff, 0x4e, 0xc9, 0x63));
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置模式

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        gestureDetector = new GestureDetector(new MyGestureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        setClickable(true);

    }

    class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        //双击
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Toast.makeText(getContext(), "双击了", Toast.LENGTH_SHORT).show();
            isSingleTap = false;
            startDoubleTapAnimation();//双击动画
            return super.onDoubleTap(e);
        }

        //单机
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(getContext(), "单击了", Toast.LENGTH_SHORT).show();
            isSingleTap = true;
            currentProgress = progress;
            startSingleTapAnimation();
            return super.onSingleTapConfirmed(e);
        }
    }

    private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable, 200);
    }

    private SingleTapRunnable singleTapRunnable = new SingleTapRunnable();

    class SingleTapRunnable implements Runnable {

        @Override
        public void run() {
            count--;
            if (count >= 0) {
                invalidate();
                handler.postDelayed(singleTapRunnable, 200);
            } else {
                handler.removeCallbacks(singleTapRunnable);
                count = 50;
            }
        }
    }

    private void startDoubleTapAnimation() {
        handler.postDelayed(doubleTapRunnable, 50);
    }

    private DoubleTapRunnable doubleTapRunnable = new DoubleTapRunnable();

    class DoubleTapRunnable implements Runnable {

        @Override
        public void run() {
            currentProgress++;
            if (currentProgress <= progress) {
                invalidate();
                handler.postDelayed(doubleTapRunnable, 50);
            } else {
                handler.removeCallbacks(doubleTapRunnable);
                currentProgress = 0;
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        bitmapCanvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
        path.reset();//重置下
        float y = (1 - (float) currentProgress / max) * height;
        path.moveTo(width, y);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, y);

        if (!isSingleTap) {
            float d = (1 - ((float) currentProgress / progress)) * 10;
            for (int i = 0; i < 5; i++) {
                path.rQuadTo(10, -d, 20, 0);//二阶贝塞尔曲线
                path.rQuadTo(10, d, 20, 0);
            }
        } else {
            float d = (float) count / 50 * 10;
            if (count % 2 == 0) {
                for (int i = 0; i < 5; i++) {
                    path.rQuadTo(20, -d, 40, 0);
                    path.rQuadTo(20, d, 40, 0);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    path.rQuadTo(20, d, 40, 0);
                    path.rQuadTo(20, -d, 40, 0);
                }
            }
        }


        path.close();
        bitmapCanvas.drawPath(path, progressPaint);
        String text = (int) (((float) currentProgress / max) * 100) + "%";
        float textwidth = textPaint.measureText(text);
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float baseLine = height / 2 - (metrics.ascent + metrics.descent) / 2;
        bitmapCanvas.drawText(text, width / 2 - textwidth / 2, baseLine, textPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
