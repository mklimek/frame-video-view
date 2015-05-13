# FrameVideoView
No more issues with VideoView. <br />
Read more: <br/>
["How to avoid flickering and black screen issues when using VideoView?"](http://blog.brightinventions.pl/frame-video-view/)<br/>

**FrameVideoView** will solve issues with VideoView. <br/>

# How to use it?

See [SimpleUsageActivity](https://github.com/mklimek/FrameVideoView/blob/master/example/src/main/java/mateuszklimek/framevideoview/example/SimpleUsageActivity.java) to more details.

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
