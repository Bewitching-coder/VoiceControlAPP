package com.example.app.mainpage;

import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VoiceButton voiceButton = findViewById(R.id.voiceButton);
        voiceButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 开始录音
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                    // 停止录音
                    stopRecording();
                    v.performClick();
                    return true;
            }
            return false;
        });
    }

    private void startRecording() {
        // TODO: 使用MediaRecorder或其他工具开始录音
    }

    private void stopRecording() {
        // TODO: 停止录音并处理录音文件
    }
}
