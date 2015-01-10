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
import android.widget.VideoView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameVideoView extends LinearLayout implements MediaPlayer.OnInfoListener {

    private VideoViewImpl impl;
    private View placeholder;
    private int videoResource;

    private static final Logger LOG = LoggerFactory.getLogger(FrameVideoView.class.getSimpleName());

    public FrameVideoView(Context context) {
        super(context);
        impl = getImplInstance(context);
    }

    public FrameVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        impl = getImplInstance(context, attrs);
    }

    private VideoViewImpl getImplInstance(Context context){
        if(Build.VERSION.SDK_INT >= 14){
            final TextureVideoViewImpl textureVideoPlayback = new TextureVideoViewImpl(context);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            final VideoViewViewImpl videoViewPlayback = new VideoViewViewImpl(context);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    private VideoViewImpl getImplInstance(Context context, AttributeSet attrs){
        if(Build.VERSION.SDK_INT >= 14){
            final TextureVideoViewImpl textureVideoPlayback = new TextureVideoViewImpl(context, attrs);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            final VideoViewViewImpl videoViewPlayback = new VideoViewViewImpl(context, attrs);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    public void setup(View root, int resource) {
        this.videoResource = resource;
        placeholder = root.findViewById(R.id.video_view_placeholder);
    }

    public void onResume(){
        LOG.trace("onResume");
        impl.onResume();
    }

    public void onPause(){
        LOG.trace("onPause");
        impl.onPause();
    }


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

    private interface VideoViewImpl {
        void onResume();
        void onPause();
    }

    @TargetApi(14)
    class TextureVideoViewImpl extends TextureView implements
            VideoViewImpl,
            MediaPlayer.OnPreparedListener,
            TextureView.SurfaceTextureListener,
            MediaPlayer.OnBufferingUpdateListener {

        private Surface surface;
        private MediaPlayer mediaPlayer;
        private boolean prepared;
        private boolean startInPrepare;

        TextureVideoViewImpl(Context context) {
            super(context);
            setSurfaceTextureListener(this);
        }

        TextureVideoViewImpl(Context context, AttributeSet attrs) {
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
                mediaPlayer.setOnInfoListener(FrameVideoView.this);
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

    class VideoViewViewImpl extends VideoView implements VideoViewImpl {

        public VideoViewViewImpl(Context context) {
            super(context);
        }

        public VideoViewViewImpl(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public VideoViewViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onResume() {

        }

        @Override
        public void onPause() {

        }
    }
}
