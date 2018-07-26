package com.fta.testfloatingservice.engine;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fta.testfloatingservice.view.FloatCircleView;
import com.fta.testfloatingservice.view.FloatMenuView;

import java.lang.reflect.Field;

/**
 * 窗口管理者的单例
 */
public class FloatViewManager {
    private static final String TAG = "FloatViewManager";

    private static FloatViewManager floatViewManager;

    private final WindowManager wm;
    private final FloatMenuView floatMenuView;

    private Context context;

    private FloatCircleView floatCircleView;

    private View.OnTouchListener circleviewTouchListener = new View.OnTouchListener() {
        private float x0;
        private float y0;
        private float startx;
        private float starty;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startx = event.getRawX();
                    x0 = event.getRawX();
                    y0 = event.getRawY();
                    starty = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    float x1 = event.getRawX();//绝对位置
                    if (x1 > getScreenWidth() / 2) {
                        params.x = getScreenWidth() - floatCircleView.width;
                    } else {
                        params.x = 0;
                    }
                    floatCircleView.setDragState(false);
                    wm.updateViewLayout(floatCircleView, params);
                    if (Math.abs(x1 - x0) > 6) {//移除onclick冲突
                        return true;
                    } else {
                        return false;
                    }
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX();
                    float y = event.getRawY();
                    float dx = x - startx;
                    float dy = y - starty;
                    params.x += dx;
                    params.y += dy;
                    floatCircleView.setDragState(true);
                    wm.updateViewLayout(floatCircleView, params);
                    startx = x;
                    starty = y;
                    break;
                default:
                    break;
            }
            return false;
        }
    };


    public int getScreenWidth() {
        return wm.getDefaultDisplay().getWidth();
    }

    public int getScreenHeight() {
        return wm.getDefaultDisplay().getHeight();
    }

    public int getStatusHeight() {
        //反射获取状态栏的高
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }

    }

    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams menuParams;

    private FloatViewManager(final Context context) {
        this.context = context;
        wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.i(TAG, "FloatViewManager-FloatViewManager: 宽：" + width + "=高=" + height);
        floatCircleView = new FloatCircleView(context);

        floatCircleView.setOnTouchListener(circleviewTouchListener);
        floatCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "onClick", Toast.LENGTH_SHORT).show();
                //隐藏circleView  显示菜单栏 开启动画
                wm.removeViewImmediate(floatCircleView);
                showFloatMenuView();
                floatMenuView.startAnimation();
            }
        });

        floatMenuView = new FloatMenuView(context);
    }


    public static FloatViewManager getInstance(Context context) {
        if (floatViewManager == null) {
            synchronized (FloatViewManager.class) {
                if (floatViewManager == null) {
                    floatViewManager = new FloatViewManager(context);
                }
            }
        }
        return floatViewManager;
    }

    /**
     * 展示浮窗小球到窗口上
     */
    public void showFloatCircleView() {
        if (params == null) {
            params = new WindowManager.LayoutParams();
            params.width = floatCircleView.width;
            params.height = floatCircleView.height;
            params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.gravity = Gravity.CENTER;
            params.x = 0;//如果忽略gravity属性，那么它表示窗口的绝对X位置。什么是gravity属性呢？简单地说，就是窗口如何停靠。当设置了 Gravity.LEFT 或 Gravity.RIGHT 之后，x值就表示到特定边的距离。
            params.y = 0;// 如果忽略gravity属性，那么它表示窗口的绝对Y位置。当设置了 Gravity.TOP 或 Gravity.BOTTOM 之后，y值就表示到特定边的距离。
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//不抢焦点
            params.format = PixelFormat.RGBA_8888;//设置背景透明
        }

        wm.addView(floatCircleView, params);


    }

    public void showFloatMenuView() {
        menuParams = new WindowManager.LayoutParams();
        menuParams.width = getScreenWidth();
        menuParams.height = getScreenHeight() - getStatusHeight();
        menuParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
//        params.gravity = Gravity.CENTER;
        menuParams.x = 0;//如果忽略gravity属性，那么它表示窗口的绝对X位置。什么是gravity属性呢？简单地说，就是窗口如何停靠。当设置了 Gravity.LEFT 或 Gravity.RIGHT 之后，x值就表示到特定边的距离。
        menuParams.y = 0;// 如果忽略gravity属性，那么它表示窗口的绝对Y位置。当设置了 Gravity.TOP 或 Gravity.BOTTOM 之后，y值就表示到特定边的距离。
        menuParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        menuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//不抢焦点
        menuParams.format = PixelFormat.RGBA_8888;//设置背景透明

        wm.addView(floatMenuView, menuParams);
    }

    public void hideFloatMenuView() {
        wm.removeView(floatMenuView);
    }
}
