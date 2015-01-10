package mateuszklimek.framevideoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameVideoView extends LinearLayout {

    private Impl impl;
    private View placeholder;
    private int videoResource;
    private ImplType type;

    private static final Logger LOG = LoggerFactory.getLogger(FrameVideoView.class.getSimpleName());

    public FrameVideoView(Context context) {
        super(context);
        impl = getImplInstance(context);
    }

    public FrameVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        impl = getImplInstance(context, attrs);
    }

    private Impl getImplInstance(Context context){
        if(Build.VERSION.SDK_INT >= 14){
            type = ImplType.TEXTURE_VIEW;
            final TextureViewImpl textureVideoPlayback = new TextureViewImpl(context);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            type = ImplType.VIDEO_VIEW;
            final VideoViewImpl videoViewPlayback = new VideoViewImpl(context);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    private Impl getImplInstance(Context context, AttributeSet attrs){
        if(Build.VERSION.SDK_INT >= 14){
            type = ImplType.TEXTURE_VIEW;
            final TextureViewImpl textureVideoPlayback = new TextureViewImpl(context, attrs);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            type = ImplType.VIDEO_VIEW;
            final VideoViewImpl videoViewPlayback = new VideoViewImpl(context, attrs);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    public void setup(View videoFrame, int resource) {
        this.videoResource = resource;
        placeholder = videoFrame.findViewById(R.id.video_view_placeholder);
    }

    public void onResume(){
        impl.onResume();
    }

    public void onPause(){
        impl.onPause();
    }

    private interface Impl {
        void onResume();
        void onPause();
    }

    public ImplType getImplType() {
        return type;
    }

    public void setImpl(Context context, ImplType implType){
        removeAllViews();
        if(implType == ImplType.TEXTURE_VIEW && Build.VERSION.SDK_INT < 14){
            implType = ImplType.VIDEO_VIEW;
            Toast.makeText(context, "Cannot use TEXTURE_VIEW impl because your device running API level 13 or lower", Toast.LENGTH_LONG).show();
        }
        type = implType;
        switch (implType){
            case TEXTURE_VIEW:
                final TextureViewImpl textureView = new TextureViewImpl(context);
                addView(textureView);
                impl = textureView;
                break;
            case VIDEO_VIEW:
                VideoViewImpl videoView = new VideoViewImpl(context);
                addView(videoView);
                impl = videoView;
                break;
        }
        onResume();
    }

    public enum ImplType{
        TEXTURE_VIEW,
        VIDEO_VIEW
    }

    @TargetApi(14)
    class TextureViewImpl extends TextureView implements
            Impl,
            MediaPlayer.OnPreparedListener,
            TextureView.SurfaceTextureListener,
            MediaPlayer.OnBufferingUpdateListener {

        private Surface surface;
        private MediaPlayer mediaPlayer;
        private boolean prepared;
        private boolean startInPrepare;

        TextureViewImpl(Context context) {
            super(context);
            setSurfaceTextureListener(this);
        }

        TextureViewImpl(Context context, AttributeSet attrs) {
            super(context, attrs);
            setSurfaceTextureListener(this);
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            LOG.trace("onPrepared isPlaying={}", mp.isPlaying());
            mp.setLooping(true);
            if(startInPrepare){
                mp.start();
                startInPrepare = false;
            }
            prepared = true;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LOG.trace("onSurfaceTextureAvailable resource={}", videoResource);
            this.surface = new Surface(surface);
            prepare();
        }

        private void prepare() {
            try {
                mediaPlayer = new MediaPlayer();
                String uriString = "android.resource://" + getContext().getPackageName() + "/" + videoResource;
                mediaPlayer.setDataSource(getContext(), Uri.parse(uriString));
                mediaPlayer.setSurface(surface);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnInfoListener(infoListener);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.prepare();
            } catch (Exception e) {
                LOG.error("cannot prepare media player with SurfaceTexture", e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            LOG.trace("onBufferingUpdate percent {}", percent);
        }

        @Override
        public void onResume() {
            if(prepared) {
                LOG.trace("start video");
                mediaPlayer.start();
            } else{
                startInPrepare = true;
            }

            if(isAvailable()){
                onSurfaceTextureAvailable(getSurfaceTexture(), 0, 0);
            }
        }

        @Override
        public void onPause() {
            placeholder.setVisibility(View.VISIBLE);
            if(mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            prepared = false;
            startInPrepare = false;
        }
    }

    class VideoViewImpl extends VideoView implements Impl, MediaPlayer.OnPreparedListener {

        public VideoViewImpl(Context context) {
            super(context);
            setOnPreparedListener(this);
        }

        public VideoViewImpl(Context context, AttributeSet attrs) {
            super(context, attrs);
            setOnPreparedListener(this);
        }

        public VideoViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setOnPreparedListener(this);
        }

        @Override
        public void onResume() {
            setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + videoResource));
            start();
        }

        @Override
        public void onPause() {
            placeholder.setVisibility(View.VISIBLE);
            stopPlayback();
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.setOnInfoListener(infoListener);
        }
    }

    private MediaPlayer.OnInfoListener infoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            LOG.trace("onInfo what={}, extra={}", what, extra);
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                LOG.trace("[MEDIA_INFO_VIDEO_RENDERING_START] placeholder GONE");
                placeholder.setVisibility(View.GONE);
                return true;
            }
            return false;
        }
    };
}
