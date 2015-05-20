# FrameVideoView
No more issues with VideoView. <br />
Read more: <br/>
["How to avoid flickering and black screen issues when using VideoView?"](http://blog.brightinventions.pl/frame-video-view/)<br/>
<br/>
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
    compile('mateuszklimek.framevideoview:framevideoview:1.0.0@aar')
    //other dependencies
}


See [SimpleUsageActivity](https://github.com/mklimek/FrameVideoView/blob/master/example/src/main/java/mateuszklimek/framevideoview/example/SimpleUsageActivity.java) to more details.

# How it works?
FrameVideoView solved flickering and black screen issues by showing placeholder in proper time.<br/>
If your device is running API level 14 or higher it will use TextureView to increase video playback performance, otherwise VideoView will be used.

# Other
If you want to execute method for particular implementation eg. `seekTo()` from `VideoView`, you can call it like that:
```java
frameVideoView.asVideoView().seekTo(x);
```
but before, it's better to check what type of implementation is used:
```java
if(videoView.getImplType() == VIDEO_VIEW){
    frameVideoView.asVideoView().seekTo(x);
}
```
