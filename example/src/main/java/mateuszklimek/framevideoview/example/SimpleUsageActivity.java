package mateuszklimek.framevideoview.example;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import mateuszklimek.framevideoview.FrameVideoView;
import mateuszklimek.framevideoview.FrameVideoViewListener;

public class SimpleUsageActivity extends Activity {

    private FrameVideoView videoView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple);
        setupFrameVideoView();
        setupOtherViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
    }

    @Override
    protected void onPause() {
        videoView.onPause();
        super.onPause();
    }

    private void setupFrameVideoView() {
        videoView = (FrameVideoView) findViewById(R.id.frame_video_view);
        videoView.setup(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fb), getResources().getColor(R.color.background));
        videoView.setFrameVideoViewListener(new FrameVideoViewListener() {
            @Override
            public void mediaPlayerPrepared(final MediaPlayer mediaPlayer) {
                SimpleUsageActivity.this.mediaPlayer = mediaPlayer;
            }
        });
    }

    private void setupOtherViews() {
        View resumeButton = findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });

        View pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        View button = findViewById(R.id.change_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SimpleUsageActivity.this, ViewPagerActivity.class));
            }
        });

        TextView info = (TextView) findViewById(R.id.info);
        info.setText(videoView.getImplType().name());
    }
}
