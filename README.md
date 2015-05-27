# FrameVideoView
No more issues with VideoView. <br />
Read more: <br/>
["How to avoid flickering and black screen issues when using VideoView?"](http://blog.brightinventions.pl/frame-video-view/)<br/>
# How to use it?
Add `http://bright.github.io/maven-repo/` to your repositories:
```groovy
repositories {
    maven {
        url "http://bright.github.io/maven-repo/"
    }
}
```
and then declare a dependency inside a module:
```groovy
dependencies {
    compile('mateuszklimek.framevideoview:framevideoview:1.1.0@aar')
    //other dependencies
}
```

See [SimpleUsageActivity](https://github.com/mklimek/FrameVideoView/blob/master/example/src/main/java/mateuszklimek/framevideoview/example/SimpleUsageActivity.java) for more details.

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
