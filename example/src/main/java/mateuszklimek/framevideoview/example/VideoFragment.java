package mateuszklimek.framevideoview.example;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mateuszklimek.framevideoview.FrameVideoView;
import mateuszklimek.framevideoview.FrameVideoViewListener;

public class VideoFragment extends Fragment {

    private FrameVideoView videoView;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int position = getArguments().getInt("position");
        View root = inflater.inflate(R.layout.simple, container, false);
        videoView = (FrameVideoView) root.findViewById(R.id.frame_video_view);
        videoView.setFrameVideoViewListener(new FrameVideoViewListener() {
            @Override
            public void mediaPlayerPrepared(MediaPlayer mediaPlayer) {
                VideoFragment.this.mediaPlayer = mediaPlayer;
            }
        });
        String uriString = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.fb;
        int backgroundColor = root.getContext().getResources().getColor(R.color.background);
        switch (position){
            case 0:
                videoView.setup(Uri.parse(uriString), backgroundColor);
                break;
            case 1:
                videoView.setup(Uri.parse(uriString), backgroundColor);
                break;
            case 2:
                videoView.setup(Uri.parse(uriString), backgroundColor);
                break;
        }
        setupOtherViews(root);
        return root;
    }

    private void setupOtherViews(View root) {
        View resumeButton = root.findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });

        View pauseButton = root.findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        Button button = (Button) root.findViewById(R.id.change_button);
        button.setText("Back");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView info = (TextView) root.findViewById(R.id.info);
        info.setText(videoView.getImplType().name());
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.onResume();
    }

    @Override
    public void onPause() {
        videoView.onPause();
        super.onPause();
    }
}
