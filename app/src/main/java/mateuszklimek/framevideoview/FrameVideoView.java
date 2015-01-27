package mateuszklimek.framevideoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameVideoView extends FrameLayout {

    public interface Impl {
        void onResume();
        void onPause();
        VideoView asVideoView();
        TextureView asTextureView();

        public enum Type {
            TEXTURE_VIEW,
            VIDEO_VIEW
        }
    }

    private Impl impl;
    private Impl.Type type;
    private View placeholder;
    private Uri videoUri;

    private static final Logger LOG = LoggerFactory.getLogger(FrameVideoView.class.getSimpleName());

    public FrameVideoView(Context context) {
        super(context);
        impl = getImplInstance(context);
        placeholder = createPlaceholder(context);
        addView(placeholder);
    }

    public FrameVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        impl = getImplInstance(context, attrs);
        placeholder = createPlaceholder(context);
        addView(placeholder);
    }

    private Impl getImplInstance(Context context){
        if(Build.VERSION.SDK_INT >= 14){
            type = Impl.Type.TEXTURE_VIEW;
            final TextureViewImpl textureVideoPlayback = new TextureViewImpl(context);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            type = Impl.Type.VIDEO_VIEW;
            final VideoViewImpl videoViewPlayback = new VideoViewImpl(context);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    private Impl getImplInstance(Context context, AttributeSet attrs){
        if(Build.VERSION.SDK_INT >= 14){
            type = Impl.Type.TEXTURE_VIEW;
            final TextureViewImpl textureVideoPlayback = new TextureViewImpl(context, attrs);
            addView(textureVideoPlayback);
            return textureVideoPlayback;
        } else{
            type = Impl.Type.VIDEO_VIEW;
            final VideoViewImpl videoViewPlayback = new VideoViewImpl(context, attrs);
            addView(videoViewPlayback);
            return videoViewPlayback;
        }
    }

    public void setup(Uri videoUri, int placeholderBackgroundColor) {
        this.videoUri = videoUri;
        placeholder.setBackgroundColor(placeholderBackgroundColor);
    }

    private View createPlaceholder(Context context) {
        View placeholder = new View(context);
        placeholder.setBackgroundColor(Color.BLACK); // default placeholder background color
        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        placeholder.setLayoutParams(params);
        return placeholder;
    }

    public void onResume(){
        impl.onResume();
    }

    public void onPause(){
        impl.onPause();
    }

    public Impl.Type getImplType() {
        return type;
    }

    public VideoView asVideoView(){
        return impl.asVideoView();
    }

    public TextureView asTextureView(){
        return impl.asTextureView();
    }

    public void setImpl(Context context, Impl.Type implType){
        removeAllViews();
        if(implType == Impl.Type.TEXTURE_VIEW && Build.VERSION.SDK_INT < 14){
            implType = Impl.Type.VIDEO_VIEW;
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
        addView(placeholder);
        onResume();
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
            LOG.trace("onSurfaceTextureAvailable resource={}", videoUri);
            this.surface = new Surface(surface);
            prepare();
        }

        private void prepare() {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(getContext(), videoUri);
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

        @Override
        public VideoView asVideoView() {
            throw new ClassCastException("FrameVideoView uses TEXTURE_VIEW implementation");
        }

        @Override
        public TextureView asTextureView() {
            return this;
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
            setVideoURI(videoUri);
            start();
        }

        @Override
        public void onPause() {
            placeholder.setVisibility(View.VISIBLE);
            stopPlayback();
        }

        @Override
        public VideoView asVideoView() {
            return this;
        }

        @Override
        public TextureView asTextureView() {
            throw new ClassCastException("FrameVideoView uses VIDEO_VIEW implementation");
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
