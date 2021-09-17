package com.huania.eew.views;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huania.eew.R;


/**
 * 加载的圈圈
 */
public class LoadingDialog extends Dialog implements DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    private ImageView mProgressWheel;
    private ObjectAnimator animator;


    public LoadingDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 取标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // 获取所有字段，包括私有字段，但不包括父类字段

        LinearLayout content = new LinearLayout(getContext());

        int dp8 = (int) (getContext().getResources().getDisplayMetrics().density * 8);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp8 * 10, dp8 * 10);
        content.setLayoutParams(params);
        content.setPadding(dp8, dp8, dp8, dp8);
        content.setGravity(Gravity.CENTER);
        content.setOrientation(LinearLayout.VERTICAL);


        mProgressWheel = new ImageView(getContext());

        mProgressWheel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)); //64dp
        mProgressWheel.setImageResource(R.mipmap.icon_loading);

        content.addView(mProgressWheel);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp8 * 8, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = dp8;
        textView.setLayoutParams(layoutParams);


        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(12);
        textView.setText("加载中..");
        content.addView(textView);

        setContentView(content);

        setOnShowListener(this);

        setOnDismissListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ObjectAnimator.ofFloat(mProgressWheel, "rotation", 0f, 720f);

        animator.setRepeatCount(-1);
        animator.setDuration(1500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }
}
