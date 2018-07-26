package com.fta.testfloatingservice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.fta.testfloatingservice.R;

/**
 * 创建浮窗小球
 */
public class FloatCircleView extends View {
    public int width = 150;
    public int height = 150;
    private String text = "50%";
    private Bitmap bitmap;

    public FloatCircleView(Context context) {
        this(context, null);
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();//初始化画笔
    }

    private Paint circlePaint;
    private Paint textPaint;

    private void initPaints() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.GREEN);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);//加粗

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.location_icon);
        //bitmap缩放(最后一个参数，是否保留状态)
        bitmap = Bitmap.createScaledBitmap(src, width, height, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (drag) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        } else {
            canvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
            float textWidtth = textPaint.measureText(text);
            float x = width / 2 - textWidtth / 2;
            Paint.FontMetrics metrics = textPaint.getFontMetrics();//取得文字规格
            float dy = -(metrics.descent + metrics.ascent) / 2;
            float y = height / 2 + dy;
            canvas.drawText(text, x, y, textPaint);//画文字需要找到文字基线的位置
        }
    }

    private boolean drag = false;

    public void setDragState(boolean dragState) {
        drag = dragState;
        invalidate();//重新绘制
    }
}
