package mateuszklimek.framevideoview;

import android.net.Uri;
import android.view.View;

interface Impl {
    void init(View placeholderView, Uri videoUri);
    void onResume();
    void onPause();
    void setFrameVideoViewListener(FrameVideoViewListener listener);
}
