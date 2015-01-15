# FrameVideoView
VideoView is the most straightforward way to show video content in layout. <br />
It took a few lines of code to setup and show for example mp4 file. <br />
It's fine when you don't care about UX too much, but when you do, things are going to be annoying.<br/><br/>
Read more: <br/>
["How to avoid flickering and black screen issues when using VideoView?"](http://127.0.0.1:4000/frame-video-view/)<br/>

**FrameVideoView** will solve issues with VideoView. <br/>

# How to use it?

Add `FrameVideoView` to layout:
```xml
  <mateuszklimek.framevideoview.FrameVideoView
    android:id="@+id/frame_video_view"
    android:layout_width="@dimen/video_width"
    android:layout_height="@dimen/video_height"
  />
```

find its instance in `Activity` and call corresponding methods in `onResume` and `onPause`:
```java
public class SampleActivity extends Activity {

  private FrameVideoView videoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple);

    String uriString = "android.resource://" + getPackageName() + "/" + R.raw.movie;
    videoView = (FrameVideoView) findViewById(R.id.frame_video_view);
    videoView.setup(Uri.parse(uriString), Color.GREEN);
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
  ```

If you want to execute method for particular implementation eg. `seekTo()` from `VideoView`, you can call it like that:
```java
frameVideoView.asVideoView().seekTo(x);
```
but before, it's better to check what type of implementation is used:
```java
if(videoView.getImplType() == TEXTURE_VIEW){
    frameVideoView.asVideoView().seekTo(x);
}
```
