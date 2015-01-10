package mateuszklimek.framevideoview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class VideoFragment extends Fragment {

    private FrameVideoView videoView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int position = getArguments().getInt("position");
        View root = inflater.inflate(R.layout.simple, container, false);
        videoView = (FrameVideoView) root.findViewById(R.id.frame_video_view);
        switch (position){
            case 0:
                videoView.setup(root.findViewById(R.id.video_frame), R.raw.fb);
                break;
            case 1:
                videoView.setup(root.findViewById(R.id.video_frame), R.raw.fb);
                break;
            case 2:
                videoView.setup(root.findViewById(R.id.video_frame), R.raw.fb);
                break;
        }
        final Button button = (Button) root.findViewById(R.id.change_button);
        button.setText("Back to simple Activity");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return root;
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
