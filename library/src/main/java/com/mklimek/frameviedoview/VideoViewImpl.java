package com.mklimek.frameviedoview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

public class VideoViewImpl extends VideoView implements Impl, MediaPlayer.OnPreparedListener {

    private View placeholderView;
    private Uri videoUri;
    private FrameVideoViewListener listener;

    public VideoViewImpl(Context context) {
        super(context);
    }

    public VideoViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(View placeholderView, Uri videoUri) {
        this.placeholderView = placeholderView;
        this.videoUri = videoUri;
        setOnPreparedListener(this);
    }

    public VideoViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnPreparedListener(this);
    }

    @Override
    public void onResume() {
        setVideoURI(videoUri);
        start();
    }

    @Override
    public void onPause() {
        placeholderView.setVisibility(View.VISIBLE);
        stopPlayback();
    }

    @Override
    public void setFrameVideoViewListener(FrameVideoViewListener listener) {
        this.listener = listener;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnInfoListener(new InfoListener(placeholderView));
        if(listener != null){
           listener.mediaPlayerPrepared(mediaPlayer);
        }
    }
}



