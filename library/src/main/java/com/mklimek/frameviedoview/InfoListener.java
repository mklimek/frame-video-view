package com.mklimek.frameviedoview;

import android.media.MediaPlayer;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoListener implements MediaPlayer.OnInfoListener {

    private View placeholderView;
    private static final Logger LOG = LoggerFactory.getLogger(InfoListener.class.getSimpleName());

    public InfoListener(View placeholderView) {
        this.placeholderView = placeholderView;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        LOG.trace("onInfo what={}, extra={}", what, extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            LOG.trace("[MEDIA_INFO_VIDEO_RENDERING_START] placeholder GONE");
            placeholderView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
