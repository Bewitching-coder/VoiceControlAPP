package com.example.app.mainpage;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import android.util.Patterns;

import utils.WebUtils;

public class VoiceCommandHandler {

    private Context mContext;

    public VoiceCommandHandler(Context context) {
        this.mContext = context;
    }

    private static final Map<String, String> URL_MAP = new HashMap<>();

    static {
        URL_MAP.put("百度网站", "www.baidu.com");
        URL_MAP.put("知乎网站", "www.zhihu.com");
        URL_MAP.put("哔哩哔哩", "www.bilibili.com");
        // 您可以继续添加其他的...
    }

    public String processVoiceCommand(String command) {
        Log.d("VoiceCommandHandler", "Entering processVoiceCommand with command: " + command);

        if (command.endsWith("。")) {
            command = command.substring(0, command.length() - 1);
        }
        if (command.startsWith("浏览") || command.startsWith("我想看") || command.startsWith("在浏览器中展示")) {
            String urlKey = command.replaceAll("浏览|我想看|在浏览器中展示", "").trim();
            String url;

            if (URL_MAP.containsKey(urlKey)) {
                url = URL_MAP.get(urlKey);
            } else {
                return null;  // 不是预定义的URL，返回null交给BottomFragment处理
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Pattern pattern = Patterns.WEB_URL;
            if (!pattern.matcher(url).matches()) {
                return null;  // URL格式不正确
            }

            Log.d("VoiceCommandHandler", "Parsed URL: " + url);
            WebUtils.openInBrowser(mContext, url);

            Log.d("VoiceCommandHandler", "Exiting processVoiceCommand with result: " + urlKey);

            return urlKey;  // 返回解析出的关键词
        } else {
            return null;  // 处理其他命令，或交给BottomFragment处理
        }
    }
}
