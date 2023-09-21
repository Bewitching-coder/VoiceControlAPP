package com.example.app.settingpage;

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
}
