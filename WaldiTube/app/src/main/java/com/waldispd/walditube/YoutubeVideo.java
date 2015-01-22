package com.waldispd.walditube;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Created by waldispd on 19.01.2015.
 */
public class YoutubeVideo
{
    public String title = "";
    public String description = "";
    public String videoId = "";
    public Boolean cached = false;
    public Boolean favorited = false;
    public int duration = 0;

    public YoutubeVideo()
    {

    }

    public YoutubeVideo(String txt1, String txt2, String videoId)
    {
        title = txt1;
        description = txt2;
        this.videoId = videoId;
    }

    public YoutubeVideo(String title, String description, String videoId, Boolean cached, Boolean favorited, int duration)
    {
        this.title = title;
        this.description = description;
        this.videoId = videoId;
        this.cached = cached;
        this.favorited = favorited;
        this.duration = duration;
    }

    public void ToFavorited() throws InterruptedException, ExecutionException, UnsupportedEncodingException {
        favorited = true;
        DataHandler.AddFavoritedVideo(this);

    }
}
