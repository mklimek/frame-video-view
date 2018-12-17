[![](https://jitpack.io/v/mklimek/frame-video-view.svg)](https://jitpack.io/#mklimek/frame-video-view)
# frame-viedo-view
Have you got any display issues using VideoView? <br />
I had too. I wrote a [blog post](http://mklimek.github.io/frame-video-view/) where you can find more details about these issues and how I solved them.<br/>

# How it works?
FrameVideoView solves flickering and black screen issues by showing placeholder on the proper time.<br/>
Placeholder is a simple `View` on top of the `VideoView`. Placeholder is visisble just after `onPause` is called and invisible when `onResume` is called.<br/>
It allows to hide `VideoView` during screen transitions which causes strange issues.
If your device is running API level 14 or higher it will use TextureView to increase video playback performance, otherwise VideoView will be used.


# How to use it?
Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Step 2. Add the dependency:
```groovy
	dependencies {
	        compile 'com.github.mklimek:frame-video-view:$RELEASE_VERSION'
	}
```
Current `$RELEASE_VERSION` is [![](https://jitpack.io/v/mklimek/frame-video-view.svg)](https://jitpack.io/#mklimek/frame-video-view)


Step 3. Add view in xml:
```xml
<com.mklimek.frameviedoview.FrameVideoView
    android:id="@+id/frame_video_view"
    android:layout_width="@dimen/video_width"
    android:layout_height="@dimen/video_height"
  />
```

Step 4. Setup resource and `FrameVideoViewListener`:
```java
frameVideoView = (FrameVideoView) findViewById(R.id.frameVideoView);
frameVideoView.setup(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fb));
frameVideoView.setFrameVideoViewListener(new FrameVideoViewListener() {
      @Override
      public void mediaPlayerPrepared(MediaPlayer mediaPlayer) {
          mediaPlayer.start();
      }
      
      @Override
      void mediaPlayerPrepareFailed(MediaPlayer mediaPlayer, String error){
      }
});
```
you can call pause, resume, looping and other methods available in `MediaPlayer`.

See [example](https://gist.github.com/mklimek/1a7e5497292b9d945ef1e143d152e312) for more details.

