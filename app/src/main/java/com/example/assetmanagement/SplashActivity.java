package com.example.assetmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assetmanagement.Login.LoginActivity;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private CustomVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.videoView);
        FrameLayout f=findViewById(R.id.frame_layout);

// Set fullscreen immersive mode
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );
//        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
//            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_FULLSCREEN |
//                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                );
//            }
//        });


// Correctly set video URI
//        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.asset_vid_two);
//        videoView.setVideoURI(video);
//        videoView.setOnPreparedListener(mp -> {
//            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//        });
//
//        videoView.setOnPreparedListener(mp -> {
//            // Get screen dimensions
//            int videoWidth = mp.getVideoWidth();
//            int videoHeight = mp.getVideoHeight();
//
//            float videoProportion = (float) videoWidth / (float) videoHeight;
//            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
//            float screenProportion = (float) screenWidth / (float) screenHeight;
//
//            ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//
//            if (videoProportion > screenProportion) {
//                lp.width = screenWidth;
//                lp.height = (int) (screenWidth / videoProportion);
//            } else {
//                lp.width = (int) (screenHeight * videoProportion);
//                lp.height = screenHeight;
//            }
//
//            videoView.setLayoutParams(lp);
//        });
//
//
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                // Start Main Activity after video ends
//                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                finish(); // Close Splash Screen
//            }
//        });
//
//        videoView.start();

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.asset_vid_two);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            int videoWidth = f.getWidth();
            int videoHeight = f.getHeight();
            videoView.setVideoSize(videoWidth, videoHeight);
            videoView.start();
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Start Main Activity after video ends
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish(); // Close Splash Screen
            }
        });

        videoView.start();
    }
}



