package com.waldispd.walditube;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dominik on 19.01.2015.
 */
public class DataHandler
{
    private static ArrayList<YoutubeVideo> favoritedVideos;

    public static void AddFavoritedVideo(YoutubeVideo video) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
        Util.DownloadAllVideoData(video.videoId);
        favoritedVideos.add(video);
    }

    public static void RemoveFavoritedVideo(YoutubeVideo video)
    {
        Util.DeleteAllVideoData(video.videoId);
        favoritedVideos.remove(video);
    }
}
