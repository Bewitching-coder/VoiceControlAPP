package com.example.app.mainpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.app.R;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView animationView = findViewById(R.id.animation_view);
        LottieAnimationView rippleAnimation = findViewById(R.id.ripple_animation);

        TextView txtPrompt = findViewById(R.id.txt_prompt);
        ImageView imgVoice = findViewById(R.id.img_voice);
        TextView txtListening = findViewById(R.id.txt_listening);
        TextView txtOpening = findViewById(R.id.txt_opening);

        // 设置ripple.json动画为循环播放，并直接开始播放
        rippleAnimation.setAnimation(R.raw.ripple);
        rippleAnimation.setRepeatCount(LottieDrawable.INFINITE);
        rippleAnimation.playAnimation();

        // 初始化并播放initial_state动画
        animationView.setAnimation(R.raw.initial_state);
        animationView.setRepeatCount(LottieDrawable.INFINITE);  // 设置动画无限循环
        animationView.playAnimation();

        animationView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    txtPrompt.setVisibility(View.GONE);
                    imgVoice.setVisibility(View.GONE);
                    txtOpening.setVisibility(View.GONE);

                    txtListening.setVisibility(View.VISIBLE);

                    animationView.cancelAnimation();  // 停止当前动画
                    animationView.setScaleX(2f);
                    animationView.setScaleY(2f);
                    animationView.setAnimation(R.raw.listening);
                    animationView.playAnimation();
                    rippleAnimation.playAnimation();
                    return true;

                case MotionEvent.ACTION_UP:
                    txtListening.setVisibility(View.GONE);
                    txtOpening.setVisibility(View.VISIBLE);

                    animationView.cancelAnimation();
                    animationView.setScaleX(1f);
                    animationView.setScaleY(1f);
                    animationView.setAnimation(R.raw.opening);
                    animationView.playAnimation();
                    return true;
            }
            return false;
        });
    }
}
