package com.waldispd.walditube;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dominik on 19.01.2015.
 */
public class Util
{
    public static MainActivity mainActivity;
    static String searchUrl = "https://gdata.youtube.com/feeds/api/videos?q=";
    static String youtubeVideoInfoUrl = "https://www.youtube.com/get_video_info?video_id=";
    static String thumbnailPath = "/sdcard/walditube/";
    static String videoPath = "/sdcard/walditube/videos/";

    public static String GetVideoIdFromLink(String link)
    {
        return link.split("/")[link.split("/").length - 1];
    }

    public static String GetThumbnailImageUrl(String videoId)
    {
        return "https://i.ytimg.com/vi/" + videoId + "/0.jpg";
    }

    public static String GetSearchUrl(String term)
    {
        return searchUrl + term;
    }

    public static String GetYoutubeVideoInfoUrl(String videoId)
    {
        return youtubeVideoInfoUrl + videoId;
    }

    public static String GetThumbnailStoragePath(String videoId)
    {
        return thumbnailPath + videoId + ".jpg";
    }

    public static String GetVideoStoragePath(String videoId)
    {
        return videoPath + videoId + ".mp4";
    }

    public static void DownloadAllVideoData(String videoId) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(GetYoutubeVideoInfoUrl(videoId));
        asyncTask.execute();
        asyncTask.get();
        String downloadPathVideo = GetDownloadPathFromVideoInfo(asyncTask.text);
        new DownloadFileAsync().execute(downloadPathVideo, videoId);
    }

    private static String GetDownloadPathFromVideoInfo(String text) throws UnsupportedEncodingException {
        //text = URLEncoder.encode(text, "UTF-8");

        final Charset UTF_8 = Charset.forName("UTF-8");
        //text = new String(text.getBytes(UTF_8), UTF_8);

        //text = URLEncoder.encode(text, "UTF-8");

        YoutubeInfo info = new YoutubeInfo(text);
        return info.GetUrlForQuality(Quality.medium);
    }

    public static void DeleteAllVideoData(String videoId)
    {
        File file = new File(GetThumbnailStoragePath(videoId));
        file.delete();

        file = new File(GetVideoStoragePath(videoId));
        file.delete();
    }
}
