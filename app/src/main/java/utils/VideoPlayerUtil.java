package utils;

import android.content.Context;
import android.content.Intent;

import com.example.app.openvideo.VideoPlayerActivity;

public class VideoPlayerUtil {

    public static void playVideo(Context context, String videoUrl) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, videoUrl);
        context.startActivity(intent);
    }
}

//调用方法
//VideoPlayerUtil.playVideo(MainActivity.this, "https://www.example.com/path_to_video.mp4");