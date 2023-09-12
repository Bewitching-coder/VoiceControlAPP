package com.example.app.settingpage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app.R;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final EditText alimeEditText = findViewById(R.id.editText);
        Button alimeSendButton = findViewById(R.id.sendButton);
        final TextView alimeResponseText = findViewById(R.id.responseText);

        // 添加Alime测试按钮点击事件
        alimeSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = alimeEditText.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    // 调用Alime测试方法
                    sendMessageToAlime(messageContent, alimeResponseText);
                }
            }
        });

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessageToAlime(String messageContent, final TextView responseTextView) {
        OkHttpClient client = new OkHttpClient();

        // 构建Alime请求体
        String requestBodyStr = "{\"messages\":[{\"content\":\"" + messageContent + "\",\"role\":\"user\"}]}";
        RequestBody requestBody = RequestBody.create(requestBodyStr, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://bjcn-api.apusai.com/ai/chat/")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Auth-Key", "ShznkDwvIcsrhUNT1BeGs8gT0mjZR5XIrSquWpfsjvLMwhvW")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理请求失败的逻辑
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();

                    // 使用Gson解析响应
                    AlimeResponse alimeResponse = new Gson().fromJson(responseStr, AlimeResponse.class);
                    final String alimeText = alimeResponse.getData().getText();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在UI线程更新UI元素
                            responseTextView.setText("Alime的响应：" + alimeText);
                        }
                    });
                } else {
                    // 处理请求失败的逻辑
                }
            }

        });

    }

    public class AlimeResponse {
        private int code;
        private String message;
        private AlimeData data;

        public AlimeData getData() {
            return data;
        }

    }
    public static class AlimeData {
        private String text;

        public String getText() {
            return text;
        }
    }
}
