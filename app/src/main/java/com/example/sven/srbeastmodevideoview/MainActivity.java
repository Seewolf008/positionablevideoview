package com.example.sven.srbeastmodevideoview;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.beastmodevideoview.SRBeastModeVideoView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SRBeastModeVideoView videoView = findViewById(R.id.surface);
        Button button = findViewById(R.id.button);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hi);
        videoView.initializeBeastMode(video, 1920);
        //videoView.setDataSource(video);
        //  preview.setSurfaceTextureListener(this);
        button.setOnClickListener(v -> {
            if (videoView.getMediaPlayer().isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
        });
    }
}
