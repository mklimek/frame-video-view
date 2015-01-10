package mateuszklimek.framevideoview;

import android.app.Activity;
import android.os.Bundle;

public class SampleUsageActivity extends Activity {

    private FrameVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_usage);
        videoView = (FrameVideoView) findViewById(R.id.frame_video_view);
        videoView.setup(findViewById(R.id.video_frame), R.raw.fb);
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
}
