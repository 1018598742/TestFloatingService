package com.fta.testfloatingservice.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.fta.testfloatingservice.R;
import com.fta.testfloatingservice.engine.FloatViewManager;

public class FloatMenuView extends LinearLayout {

    private LinearLayout linearLayout;
    private TranslateAnimation translateAnimation;

    public FloatMenuView(Context context) {
        this(context, null);
    }

    public FloatMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View root = View.inflate(context, R.layout.float_menu_view, null);
        linearLayout = ((LinearLayout) root.findViewById(R.id.ll));
        //动画
        translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(500);//时长
        translateAnimation.setFillAfter(true);//是否保留最后状态
        linearLayout.setAnimation(translateAnimation);

        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatViewManager manager = FloatViewManager.getInstance(getContext());
                manager.hideFloatMenuView();
                manager.showFloatCircleView();
                return false;
            }
        });
        addView(root);
    }


    public void startAnimation() {
        translateAnimation.start();
    }

}
