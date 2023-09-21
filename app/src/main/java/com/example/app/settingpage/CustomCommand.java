package com.example.app.settingpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app.AppConstants;


public class CustomCommand {

    public static final int TYPE_WEB_PAGE = 1;
    public static final int TYPE_APP = 2;
    public static final int TYPE_VIDEO = 3;

    private String title;    // 用于展示的标题
    private String command;  // 能语音识别出比对的命令内容
    private int type;        // 1 网页, 2 app, 3 视频
    private String keyWords; // 网页对应url，app对应包名，视频对应路径（本地和在线）

    // Constructor
    public CustomCommand(String title, String command, int type, String keyWords) {
        this.title = title;
        this.command = command;
        this.type = type;
        this.keyWords = keyWords;
    }
    public CustomCommand() {
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getCommand() {
        return command;
    }

    public int getType() {
        return type;
    }

    public String getKeyWords() {
        return keyWords;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }
    public void saveToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.COMMAND_KEY, command);
        editor.apply();
    }

    public String loadCommandFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.COMMAND_KEY, command);
    }
    public void saveTypeToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AppConstants.TYPE_KEY, type);
        editor.apply();
    }

    public int loadTypeFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(AppConstants.TYPE_KEY, type);
    }


    public void saveKeywordsToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.KEYWORDS_KEY, keyWords);
        editor.apply();
    }

    public String loadKeywordsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.KEYWORDS_KEY, null);
    }


}
