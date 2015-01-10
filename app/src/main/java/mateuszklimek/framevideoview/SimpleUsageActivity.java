package mateuszklimek.framevideoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SimpleUsageActivity extends Activity {

    private FrameVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple);

        videoView = (FrameVideoView) findViewById(R.id.frame_video_view);
        videoView.setup(findViewById(R.id.video_frame), R.raw.fb);

        setupButton();
    }

    private void setupButton() {
        View button = findViewById(R.id.change_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SimpleUsageActivity.this, ViewPagerActivity.class));
            }
        });
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
