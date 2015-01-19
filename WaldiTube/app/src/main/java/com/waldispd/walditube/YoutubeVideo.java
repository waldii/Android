package com.waldispd.walditube;

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

    public YoutubeVideo()
    {

    }

    public YoutubeVideo(String txt1, String txt2)
    {
        title = txt1;
        description = txt2;
    }

    public YoutubeVideo(String title, String description, String videoId, Boolean cached, Boolean favorited)
    {
        this.title = title;
        this.description = description;
        this.videoId = videoId;
        this.cached = cached;
        this.favorited = favorited;
    }
}
