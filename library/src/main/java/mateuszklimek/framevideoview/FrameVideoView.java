package mateuszklimek.framevideoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameVideoView extends FrameLayout {

    private Impl impl;
    private ImplType implType;
    private View placeholderView;
    private Uri videoUri;

    private static final Logger LOG = LoggerFactory.getLogger(FrameVideoView.class.getSimpleName());

    public FrameVideoView(Context context) {
        super(context);
        placeholderView = createPlaceholderView(context);
        impl = getImplInstance(context);
        addView(placeholderView);
    }

    public FrameVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        placeholderView = createPlaceholderView(context);
        impl = getImplInstance(context, attrs);
        addView(placeholderView);
    }

    private Impl getImplInstance(Context context){
        if(Build.VERSION.SDK_INT >= 14){
            implType = ImplType.TEXTURE_VIEW;
            final TextureViewImpl textureVideoImpl = new TextureViewImpl(context);
            textureVideoImpl.init(placeholderView, videoUri);
            addView(textureVideoImpl);
            return textureVideoImpl;
        } else{
            implType = ImplType.VIDEO_VIEW;
            final VideoViewImpl videoViewImpl = new VideoViewImpl(context);
            addView(videoViewImpl);
            return videoViewImpl;
        }
    }

    private Impl getImplInstance(Context context, AttributeSet attrs){
        if(Build.VERSION.SDK_INT >= 14){
            implType = ImplType.TEXTURE_VIEW;
            final TextureViewImpl textureVideoImpl = new TextureViewImpl(context, attrs);
            textureVideoImpl.init(placeholderView, videoUri);
            addView(textureVideoImpl);
            return textureVideoImpl;
        } else{
            implType = ImplType.VIDEO_VIEW;
            final VideoViewImpl videoViewImpl = new VideoViewImpl(context, attrs);
            videoViewImpl.init(placeholderView, videoUri);
            addView(videoViewImpl);
            return videoViewImpl;
        }
    }

    public void setup(Uri videoUri) {
        this.videoUri = videoUri;
        impl.init(placeholderView, videoUri);
    }
    
    public void setup(Uri videoUri, int placeholderBackgroundColor) {
        this.videoUri = videoUri;
        placeholderView.setBackgroundColor(placeholderBackgroundColor);
        impl.init(placeholderView, videoUri);
    }

    @SuppressLint("NewApi")
    public void setup(Uri videoUri, Drawable placeholderDrawable) {
        this.videoUri = videoUri;
        if(Build.VERSION.SDK_INT < 16) {
            placeholderView.setBackgroundDrawable(placeholderDrawable);
        } else{
            placeholderView.setBackground(placeholderDrawable);
        }
    }

    private View createPlaceholderView(Context context) {
        View placeholder = new View(context);
        placeholder.setBackgroundColor(Color.BLACK); // default placeholderView background color
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

    public ImplType getImplType() {
        return implType;
    }

    public View getPlaceholderView() {
        return placeholderView;
    }

    public void setFrameVideoViewListener(FrameVideoViewListener listener){
        impl.setFrameVideoViewListener(listener);
    }

    public void setImpl(Context context, ImplType implImplType){
        removeAllViews();
        if(implImplType == ImplType.TEXTURE_VIEW && Build.VERSION.SDK_INT < 14){
            implImplType = ImplType.VIDEO_VIEW;
            Toast.makeText(context, "Cannot use TEXTURE_VIEW impl because your device running API level 13 or lower", Toast.LENGTH_LONG).show();
        }
        this.implType = implImplType;
        switch (implImplType){
            case TEXTURE_VIEW:
                final TextureViewImpl textureViewImpl = new TextureViewImpl(context);
                textureViewImpl.init(placeholderView, videoUri);
                addView(textureViewImpl);
                impl = textureViewImpl;
                break;
            case VIDEO_VIEW:
                VideoViewImpl videoViewImpl = new VideoViewImpl(context);
                videoViewImpl.init(placeholderView, videoUri);
                addView(videoViewImpl);
                impl = videoViewImpl;
                break;
        }
        addView(placeholderView);
        onResume();
    }

}
