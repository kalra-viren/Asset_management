package com.example.assetmanagement;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {
    private int videoWidth = 0;
    private int videoHeight = 0;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoSize(int width, int height) {
        videoWidth = width;
        videoHeight = height;
        requestLayout(); // Request layout update to apply new dimensions
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (videoWidth > 0 && videoHeight > 0) {
            float aspectRatio = (float) videoWidth / videoHeight;
            float layoutRatio = (float) width / height;

            if (layoutRatio > aspectRatio) {
                width = (int) (height * aspectRatio);
            } else {
                height = (int) (width / aspectRatio);
            }
        }

        setMeasuredDimension(width, height);
    }
}

