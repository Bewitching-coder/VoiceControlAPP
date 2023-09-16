package com.example.app.mainpage;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.app.R;
import com.example.app.settingpage.SettingActivity;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import androidx.core.content.ContextCompat;

import android.widget.Toast;
import android.Manifest;

import okhttp3.Call;
import utils.AlimeHelper;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;


public class BottomFragment extends Fragment {
    private SpeechRecognizer mIat;  // 使用讯飞的语音识别类
    private String recognizedText = "";
    private final String MALE_VOICE = "aisjiuxu";
    private StringBuilder recognizedStringBuilder = new StringBuilder();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("BottomFragment", "onCreateView called.");
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("BottomFragment", "onActivityCreated called.");
        initSpeechRecognizer();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BottomFragment", "onCreate called.");
    }

    private void initSpeechRecognizer() {
        Log.d("BottomFragment", "Initializing SpeechRecognizer.");
        // 确保 getContext() 不为 null
        if (getContext() == null) {
            Log.e("BottomFragment", "Context is null. Cannot initialize SpeechRecognizer.");
            return;
        }
        // 初始化讯飞语音识别
        String APPID = "a16921d1";
        SpeechUtility.createUtility(getContext(), SpeechConstant.APPID + "=" + APPID);
        mIat = SpeechRecognizer.createRecognizer(getContext(), new MyInitListener());
        if (mIat != null) {
            Log.d("BottomFragment", "SpeechRecognizer created successfully.");
            mIat.setParameter(SpeechConstant.DOMAIN, "iat");
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
            String FEMALE_VOICE = "xiaoyan";
            mIat.setParameter(SpeechConstant.VOICE_NAME, FEMALE_VOICE);
        } else {
            Log.e("BottomFragment", "Failed to create SpeechRecognizer.");
        }
    }

    private class MyInitListener implements InitListener {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("BottomFragment", "Initialization of SpeechRecognizer failed, error code: " + code);
            } else {
                Log.d("BottomFragment", "Initialization of SpeechRecognizer succeeded.");
            }
        }
    }



    private static final int RECORD_AUDIO_REQUEST_CODE = 101;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestMicrophonePermission();

        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        LottieAnimationView rippleAnimation = view.findViewById(R.id.ripple_animation);
        LottieAnimationView sayAnimation = view.findViewById(R.id.top_animation);

        TextView txtPrompt = view.findViewById(R.id.txt_prompt);
        ImageView imgVoice = view.findViewById(R.id.img_voice);
        TextView txtListening = view.findViewById(R.id.txt_listening);
        TextView txtOpening = view.findViewById(R.id.txt_opening);
        FrameLayout speechOutput = view.findViewById(R.id.speech_output);
        TextView txtSpeechResult = view.findViewById(R.id.txt_speech_result);

        rippleAnimation.setAnimation(R.raw.ripple);
        rippleAnimation.setRepeatCount(LottieDrawable.INFINITE);
        rippleAnimation.playAnimation();

        animationView.setAnimation(R.raw.initial_state);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();

        sayAnimation.setAnimation(R.raw.you_can_say);
        sayAnimation.setRepeatCount(LottieDrawable.INFINITE);
        sayAnimation.playAnimation();

        animationView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestMicrophonePermission();
                        return true; // 结束处理触摸事件
                    }
                    txtPrompt.setVisibility(View.GONE);
                    imgVoice.setVisibility(View.GONE);
                    txtOpening.setVisibility(View.GONE);
                    sayAnimation.setVisibility(View.GONE);

                    txtListening.setVisibility(View.VISIBLE);
                    speechOutput.setVisibility(View.VISIBLE);
                    if (txtSpeechResult != null) {
                        txtSpeechResult.setText(recognizedText);
                    }


                    animationView.cancelAnimation();
                    animationView.setScaleX(2f);
                    animationView.setScaleY(2f);
                    animationView.setAnimation(R.raw.listening);
                    animationView.playAnimation();
                    rippleAnimation.playAnimation();
                    recognizedStringBuilder.setLength(0);

                    // Start the voice recognition here
                    if (mIat != null) {
                        mIat.startListening(mRecognizerListener);
                    } else {
                        Log.e("BottomFragment", "SpeechRecognizer is null during touch event.");
                        return false;
                    }

                    return true;

                case MotionEvent.ACTION_UP:
                    txtListening.setVisibility(View.GONE);
                    txtOpening.setVisibility(View.VISIBLE);
                    sayAnimation.setVisibility(View.GONE);

                    animationView.cancelAnimation();
                    animationView.setScaleX(1f);
                    animationView.setScaleY(1f);
                    animationView.setAnimation(R.raw.opening);
                    animationView.playAnimation();

                    // 仅当mIat不为空时，我们才停止监听
                    if (mIat != null && mIat.isListening()) {
                        mIat.stopListening();
                    } else {
                        Log.e("BottomFragment", "SpeechRecognizer is null or not running during touch event.");
                    }


                    // 重新设置txtSpeechResult的文本
                    if (txtSpeechResult != null) {
                        txtSpeechResult.setText(recognizedText);
                    }
                    return true;
            }
            return false;
        });
    }

    private void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限已获得，可以开始语音识别
                } else {
                    // 权限被拒绝，告知用户为什么需要这个权限
                    Toast.makeText(getContext(), "需要麦克风权限来进行语音识别", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (recognizerResult != null) {
                String jsonResult = recognizerResult.getResultString();
                String partialText = parseResult(jsonResult);
                if (isLast) {
                    // 识别结束，传递文字给AliMe
                    sendTextToAlime(recognizedStringBuilder.toString());
                }

                // 添加这个部分的文本到StringBuilder中
                recognizedStringBuilder.append(partialText);

                // 添加Log以查看解析结果
                Log.d("RecognizerListener", "Parsed text: " + recognizedStringBuilder.toString());

                // 更新UI
                requireActivity().runOnUiThread(() -> {
                    TextView txtSpeechResult = requireView().findViewById(R.id.txt_speech_result);
                    txtSpeechResult.setText(recognizedStringBuilder.toString());
                });
            }
        }

        private void sendTextToAlime(String recognizedText) {
            String autobiography = "你假装是一个律师跟我说话";
            AlimeHelper.sendMessageToAlime(recognizedText, autobiography, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    // 处理请求失败的逻辑
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        // 使用Gson解析响应，你可能需要添加Gson的import声明
                        SettingActivity.AlimeResponse alimeResponse = new Gson().fromJson(responseStr, SettingActivity.AlimeResponse.class);
                        String alimeReply = alimeResponse.getData().getText();

                        // 将 Alime 的回复追加到 recognizedStringBuilder 并更新 UI
                        recognizedStringBuilder.append("\nAliMe: ").append(alimeReply);

                        requireActivity().runOnUiThread(() -> {
                            TextView txtSpeechResult = requireView().findViewById(R.id.txt_speech_result);
                            txtSpeechResult.setText(recognizedStringBuilder.toString());
                        });
                    } else {
                        // 处理请求失败的逻辑
                    }
                }
            });
        }



        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d("RecognizerListener", "onVolumeChanged: volume = " + volume);
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d("RecognizerListener", "onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("RecognizerListener", "onEndOfSpeech");
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d("SpeechRecognition", "Error: " + speechError.getPlainDescription(true));
            Log.e("RecognizerListener", "Error occurred: " + speechError.getPlainDescription(true));

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            Log.d("RecognizerListener", "onEvent: eventType = " + eventType);
        }

        // 其他需要实现的方法，如onVolumeChanged, onEndOfSpeech等

        // ... [其他的RecognizerListener方法]
    };

    private String parseResult(String json) {
        StringBuilder ret = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ws = jsonObject.getJSONArray("ws");
            for (int i = 0; i < ws.length(); i++) {
                JSONArray cw = ws.getJSONObject(i).getJSONArray("cw");
                for (int j = 0; j < cw.length(); j++) {
                    ret.append(cw.getJSONObject(j).getString("w"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mIat != null && mIat.isListening()) {
                mIat.stopListening();
            }
        } catch (Exception e) {
            Log.e("BottomFragment", "Error in onPause: " + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mIat != null && mIat.isListening()) {
                mIat.stopListening();
                mIat.destroy();
            }
        } catch (Exception e) {
            Log.e("BottomFragment", "Error in onDestroy: " + e.toString());
        }
    }

}
