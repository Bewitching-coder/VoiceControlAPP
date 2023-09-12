package com.example.app.openvideo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.app.R;

public class VideoPlayerActivity extends Activity {

    public static final String EXTRA_VIDEO_URL = "video_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VideoView videoView = findViewById(R.id.videoView);
        String videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();
    }
}
