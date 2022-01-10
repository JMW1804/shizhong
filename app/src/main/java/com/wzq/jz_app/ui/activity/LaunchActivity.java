package com.wzq.jz_app.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.wzq.jz_app.R;

/**
 * author ：jmw
 * startPage
 */

public class LaunchActivity extends AppCompatActivity {

    private ImageView mImgSlogan01;
    private ImageView mImgSlogan02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        mImgSlogan01 = findViewById(R.id.img_slogan_01);
        mImgSlogan02 = findViewById(R.id.img_slogan_02);

        initView();

        /*View target = findViewById(R.id.launch);
        //第一个参数--target：你要对那个View绑定动画，今天我们要对ImageView绑定动画
        //第二个参数---propertyName:你要执行什么动画---动画的属性名称
        //缩放动画：scaleX
        //渐变动画：alpha
        //第三个参数--动画变化范围（例如：缩放动画变化范围0.0-1.0之间）
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, "alpha", 0.5f, 1.0f);
//        //设置动画执行的时间（企业级开发标准：执行时间一般情况2-3秒）
        objectAnimator.setDuration(1000);
//        //启动动画
        objectAnimator.start();

        //扩展知识点---设计模式---适配器模式
        //项目开发需要定义很多的接口
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startActivity(new Intent(LaunchActivity.this, MainActivity1.class));
                finish();
            }
        });*/
    }

    protected void initView() {

        final TranslateAnimation
            animation_01 = new TranslateAnimation(0, 0, 0, dip2px(this, 40));
        final TranslateAnimation animation_02 =
            new TranslateAnimation(0, 0, 0, -dip2px(this, 40));
        setupAnim(animation_01);
        setupAnim(animation_02);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            mImgSlogan01.setVisibility(View.VISIBLE);
            mImgSlogan01.startAnimation(animation_01);
            handler.postDelayed(() -> {
                mImgSlogan02.setVisibility(View.VISIBLE);
                mImgSlogan02.startAnimation(animation_02);
                handler.postDelayed(() -> {
                    startActivity(new Intent(LaunchActivity.this, MainActivity1.class));
                    finish();
                }, 1000);
            }, 400);
        }, 300);
    }

    private void setupAnim(TranslateAnimation anim){
        anim.setDuration(400);
        anim.setFillAfter(true);
        anim.setRepeatCount(0);
        anim.setInterpolator(new DecelerateInterpolator());
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
