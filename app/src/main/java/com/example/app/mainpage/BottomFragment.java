package com.example.app.mainpage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.app.R;
import com.example.app.settingpage.SettingActivity;
import com.example.app.settingpage.VoiceRecognitionSettingsActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.app.settingpage.CustomCommand;

import java.util.ArrayList;
import java.util.List;

public class BottomFragment extends Fragment {
    private SpeechRecognizer mIat;  // 使用讯飞的语音识别类
    private String recognizedText = "";
    private final String MALE_VOICE = "aisjiuxu";
    private StringBuilder recognizedStringBuilder = new StringBuilder();

    private SpeechSynthesizer mTts;
    private LottieAnimationView animationView;
    private LottieAnimationView rippleAnimation;
    private LottieAnimationView sayAnimation;
    private TextView txtPrompt;
    private ImageView imgVoice;
    private FrameLayout speechOutput;

    private List<CustomCommand> commandsList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    String savedTitle;
    String savedType;
    String savedAddress;
    String savedCommand;


// ... 其他相关的元素


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("BottomFragment", "onCreateView called.");
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);
        loadInstalledApps();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("BottomFragment", "onActivityCreated called.");
        initSpeechRecognizer();
        initSpeechSynthesizer();  // 初始化语音合成
        mVoiceCommandHandler = new VoiceCommandHandler(getActivity());

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

        // 初始化 SharedPreferences 和获取数据
        sharedPreferences = getActivity().getSharedPreferences(VoiceRecognitionSettingsActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        savedTitle = sharedPreferences.getString("savedTitleKey", null);
        savedType = sharedPreferences.getString("savedTypeKey", null);
        savedAddress = sharedPreferences.getString("savedAddressKey", null);
        savedCommand = sharedPreferences.getString("savedCommandKey", null);


        // 获取整个命令列表
        String jsonCommands = sharedPreferences.getString(VoiceRecognitionSettingsActivity.COMMANDS_LIST, "");
        Type type = new TypeToken<ArrayList<CustomCommand>>() {
        }.getType();
        ArrayList<CustomCommand> commands = new Gson().fromJson(jsonCommands, type);

        // 如果需要，您可以从commands列表中提取任何您需要的信息
        if (commands != null && commands.size() > 0) {
            CustomCommand firstCommand = commands.get(0);
            String title = firstCommand.getTitle();
            int commandType = firstCommand.getType();
            String address = firstCommand.getKeyWords();

            // ... 使用title, commandType, 和 address
        }


        ImageButton btnSettings = view.findViewById(R.id.aboutButton);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        animationView = view.findViewById(R.id.animation_view);
        rippleAnimation = view.findViewById(R.id.ripple_animation);
        sayAnimation = view.findViewById(R.id.top_animation);
        imgVoice = view.findViewById(R.id.img_voice);
        speechOutput = view.findViewById(R.id.speech_output);


        txtPrompt = view.findViewById(R.id.txt_prompt);
        TextView txtListening = view.findViewById(R.id.txt_listening);
        TextView txtOpening = view.findViewById(R.id.txt_opening);
        TextView txtSpeechResult = view.findViewById(R.id.txt_speech_result);
        txtSpeechResult.setTextAppearance(requireContext(), R.style.VoiceInputTextStyle);


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
                recognizedStringBuilder.append(partialText);

                if (isLast) {
                    String fullRecognizedText = recognizedStringBuilder.toString();
                    if (fullRecognizedText.startsWith("打开") ||
                            fullRecognizedText.startsWith("浏览") ||
                            fullRecognizedText.startsWith("我想看") ||
                            fullRecognizedText.startsWith("在浏览器中展示")) {
                        handleVoiceCommand(fullRecognizedText); // 如果指令是 "打开..." 则尝试启动对应应用
                    } else {
                        sendTextToAlime(fullRecognizedText); // 其他情况发送给AliMe
                    }

                    handleCustomCommands(fullRecognizedText);
                }

                Log.d("RecognizerListener", "Parsed text: " + recognizedStringBuilder.toString());

                requireActivity().runOnUiThread(() -> {
                    TextView txtSpeechResult = requireView().findViewById(R.id.txt_speech_result);
                    txtSpeechResult.setText(recognizedStringBuilder.toString());
                });
            }
        }


        @Override
        public void onError(SpeechError speechError) {

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
                        Log.d("BottomFragment", "Server Response: " + responseStr);

                        // 使用Gson解析响应
                        SettingActivity.AlimeResponse alimeResponse = new Gson().fromJson(responseStr, SettingActivity.AlimeResponse.class);

                        if (alimeResponse != null && alimeResponse.getData() != null) {
                            String alimeReply = alimeResponse.getData().getText();
                            speakOutLoud(alimeReply);  // 使用语音合成播报Alime的回答

                            // 将 Alime 的回复追加到 recognizedStringBuilder 并更新 UI
                            recognizedStringBuilder.append("\nAliMe: ").append(alimeReply);

                            requireActivity().runOnUiThread(() -> {
                                TextView txtSpeechResult = requireView().findViewById(R.id.txt_speech_result);
                                txtSpeechResult.setText(recognizedStringBuilder.toString());
                            });
                        } else {
                            // 你可能想在这里添加一些错误处理代码，例如日志记录或UI提示
                            Log.e("BottomFragment", "alimeResponse or alimeResponse.getData() is null.");
                        }
                    } else {
                        // 处理请求失败的逻辑
                    }

                }
            });
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // You can leave this method empty if you don't want to handle volume changes.
        }


        @Override
        public void onBeginOfSpeech() {
            Log.d("RecognizerListener", "onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {

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

    private Map<String, String> appNameToPackageMap = new HashMap<>();

    private void loadInstalledApps() {
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        for (ApplicationInfo app : apps) {
            String appName = pm.getApplicationLabel(app).toString();
            String packageName = app.packageName;
            appNameToPackageMap.put(normalizeString(appName), packageName);
            Log.d("BottomFragment", "Normalized Name: " + normalizeString(appName) + ", Loaded App: " + appName + " with Package: " + packageName);
            Log.d("BottomFragment", "Loaded App: " + appName + " with Package: " + packageName);
        }
    }

    private void handleVoiceCommand(String command) {
        Log.d("VoiceCommand", "Received command: " + command);
        command = command.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "");
        Log.d("VoiceCommand", "Cleaned command: " + command);
        CustomCommand customCommand = new CustomCommand();
        String savedCommand = customCommand.loadCommandFromSharedPreferences(getActivity());
        Log.d("VoiceCommand", "savedCommand: " + savedCommand);
        String savedType = Integer.toString(customCommand.loadTypeFromSharedPreferences(getActivity()));
        Log.d("VoiceCommand", "Saved type: " + savedType);
        if (command != null && savedCommand != null && command.equals(savedCommand)) {
            Log.d("VoiceCommand", "Saved type: " + savedType);
            switch (savedType) {
                case "打开网页":
                    // 打开网页
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(savedAddress));
                    startActivity(browserIntent);
                    break;

                case "打开应用":
                    // 打开应用
                    try {
                        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(savedAddress);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                            Toast.makeText(getActivity(), "应用未安装或未找到", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "打开应用时出错", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "打开视频":
                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    if (savedAddress.startsWith("https://") || savedAddress.startsWith("http://")) {
                        // 在线视频
                        videoIntent.setDataAndType(Uri.parse(savedAddress), "video/*");
                    } else if (savedAddress.startsWith("content://")) {
                        // 相册路径
                        videoIntent.setDataAndType(Uri.parse(savedAddress), "video/*");
                    } else {
                        // SD卡路径或其他路径
                        videoIntent.setDataAndType(Uri.fromFile(new File(savedAddress)), "video/*");
                    }
                    startActivity(videoIntent);
                    break;

                default:
                    Toast.makeText(getActivity(), "不支持的命令类型", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // 这里处理其他的命令，比如原始的 "打开应用名" 逻辑
        if (command.startsWith("打开")) {
            String appName = command.substring(2).trim();
            appName = appName.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "");
            Log.d("VoiceCommand", "Extracted app name: " + appName);
            String packageName = appNameToPackageMap.get(appName);
            Log.d("VoiceCommand", "Mapped package name: " + packageName);
            if (packageName != null) {
                try {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    } else {
                        Log.e("VoiceCommand", "Launch intent is null for package: " + packageName);
                        Toast.makeText(getActivity(), "应用未安装或未找到", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("VoiceCommand", "Error while opening app", e);
                    Toast.makeText(getActivity(), "打开应用时出错", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("VoiceCommand", "No package mapped for app name: " + appName);
                Toast.makeText(getActivity(), "未识别或未安装的应用", Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("输入的格式不正确，请重新输入，您可以说浏览/我想看/在浏览器中展示 某某某网站")
                    .setPositiveButton("确定", null)
                    .create()
                    .show();
        }
    }

    private void launchAppByPackageName(String packageName) {
        try {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "无法启动应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("BottomFragment", "Error in launching app: " + e.toString());
            Toast.makeText(getActivity(), "启动应用时出错", Toast.LENGTH_SHORT).show();
        }
    }

    private String normalizeString(String input) {
        return input.toLowerCase().replace(" ", "").replace("。", "");
    }

    private void initSpeechSynthesizer() {
        Log.d("BottomFragment", "Initializing SpeechSynthesizer.");
        mTts = SpeechSynthesizer.createSynthesizer(getContext(), new MyInitListener());

        if (mTts != null) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, MALE_VOICE);
            mTts.setParameter(SpeechConstant.SPEED, "50");
            mTts.setParameter(SpeechConstant.VOLUME, "80");
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        } else {
            Log.e("BottomFragment", "Failed to create SpeechSynthesizer.");
        }
    }

    private void speakOutLoud(String text) {
        if (mTts != null) {
            mTts.startSpeaking(text, new MySynthesizerListener(null));
        } else {
            Log.e("BottomFragment", "SpeechSynthesizer is null during speak.");
        }
    }

    private void speakAndLaunchApp(String text, String packageName) {
        if (mTts != null) {
            mTts.startSpeaking(text, new MySynthesizerListener(packageName));
        } else {
            Log.e("BottomFragment", "SpeechSynthesizer is null during speak.");
        }
    }

    private class MySynthesizerListener implements SynthesizerListener {
        private String mPackageName;

        public MySynthesizerListener(String packageName) {
            this.mPackageName = packageName;
        }

        @Override
        public void onSpeakBegin() {
            // ...
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // ...
        }

        @Override
        public void onSpeakPaused() {
            // ...
        }

        @Override
        public void onSpeakResumed() {
            // ...
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // ...
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                // 如果播报完成，并且没有错误，那么启动应用
                launchAppByPackageName(mPackageName);
            } else {
                Log.e("BottomFragment", "Error during speech: " + error.toString());
                Toast.makeText(getActivity(), "语音播报错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj2) {
            // 你的实现代码...
        }
    }


    private VoiceCommandHandler mVoiceCommandHandler;

    private void resetUIElements() {
        // 重置Lottie动画
        if (animationView != null) {
            animationView.cancelAnimation();
            animationView.setAnimation(R.raw.initial_state);
            animationView.setRepeatCount(LottieDrawable.INFINITE);
            animationView.playAnimation();
        }

        if (rippleAnimation != null) {
            rippleAnimation.cancelAnimation();
            rippleAnimation.setAnimation(R.raw.ripple);
            rippleAnimation.setRepeatCount(LottieDrawable.INFINITE);
            rippleAnimation.playAnimation();
        }

        // 重置TextView
        if (txtPrompt != null) {
            txtPrompt.setVisibility(View.VISIBLE);
        }
        if (sayAnimation != null) {
            sayAnimation.setVisibility(View.VISIBLE);
            sayAnimation.setAnimation(R.raw.you_can_say);
            sayAnimation.setRepeatCount(LottieDrawable.INFINITE);
            sayAnimation.playAnimation();
        }

        if (imgVoice != null) {
            imgVoice.setVisibility(View.VISIBLE);
        }

        // 将speech_output隐藏
        if (speechOutput != null) {
            speechOutput.setVisibility(View.GONE);
        }
    }

    public void resetUIFromExternal() {
        resetUIElements();
    }

    private void handleCustomCommands(String recognizedText) {
        for (CustomCommand cmd : commandsList) {
            if (recognizedText.contains(cmd.getCommand())) {
                switch (cmd.getType()) {
                    case CustomCommand.TYPE_WEB_PAGE:
                        // 打开网页
                        openWebPage(cmd.getKeyWords());
                        break;
                    case CustomCommand.TYPE_APP:
                        // 打开应用
                        openApp(cmd.getKeyWords());
                        break;
                    case CustomCommand.TYPE_VIDEO:
                        // 打开视频
                        openVideo(cmd.getKeyWords());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void openWebPage(String url) {
        // Implement your code to open a web page using the provided URL
    }

    private void openApp(String packageName) {
        // Implement your code to open an app using the provided package name
    }

    private void openVideo(String path) {
        // Implement your code to open a video using the provided path
    }


}
