# FrameVideoView
No more issues with VideoView. <br />
Read more: <br/>
["How to avoid flickering and black screen issues when using VideoView?"](http://blog.brightinventions.pl/frame-video-view/)<br/>
# How to use it?
Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Step 2. Add the dependency
```groovy
	dependencies {
	        compile 'com.github.mklimek:frame-video-view:1.2.0'
	}
```

See [example](https://gist.github.com/mklimek/1a7e5497292b9d945ef1e143d152e312) for more details.

# How it works?
FrameVideoView solved flickering and black screen issues by showing placeholder in proper time.<br/>
If your device is running API level 14 or higher it will use TextureView to increase video playback performance, otherwise VideoView will be used.

# Manipulate video playback
Call `setFrameVideoViewListener` method to get instance of `MediaPlayer` by call:
```java
frameVideoView.setFrameVideoViewListener(new FrameVideoViewListener() {
            @Override
            public void mediaPlayerPrepared(final MediaPlayer mediaPlayer) {
                MainActivity.this.mediaPlayer = mediaPlayer;
            }
        });
```
after that you can call pause, resume, looping and other methods available in `MediaPlayer`.
