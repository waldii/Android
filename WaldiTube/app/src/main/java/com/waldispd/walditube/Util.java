package com.waldispd.walditube;

import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dominik on 19.01.2015.
 */
public class Util
{
    public static MainActivity mainActivity;
    static String searchUrl = "https://gdata.youtube.com/feeds/api/videos?q=";
    static String youtubeVideoInfoUrl = "https://www.youtube.com/get_video_info?video_id=";
    static String thumbnailPath = "/sdcard/walditube/thumbnails/";
    static String videoPath = "/sdcard/walditube/videos/";

    public static String GetUft8EncodedString(String stringToEncode) throws UnsupportedEncodingException
    {
        return URLDecoder.decode(stringToEncode, "UTF-8");
    }

    public static void MakeToast(String text, Boolean timeLong)
    {
        int duration = Toast.LENGTH_SHORT;
        if (timeLong){
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }
        Toast.makeText(mainActivity.getApplicationContext(), text, duration);
    }

    public static String GetDurationString(int duration)
    {
        int hour = duration / 3600;
        duration = duration - (hour * 3600);
        int minutes = duration / 60;
        duration = duration - (minutes * 60);
        return String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", duration);
    }

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
        return thumbnailPath + videoId + ".jpeg";
    }

    public static String GetVideoStoragePath(String videoId)
    {
        return videoPath + videoId + ".mp4";
    }

    public static void DownloadAllVideoData(String videoId) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(GetYoutubeVideoInfoUrl(videoId));
        asyncTask.execute();
        asyncTask.get();

        String textEnc = URLDecoder.decode(asyncTask.text, "UTF-8");

        YoutubeInfo info = new YoutubeInfo(textEnc, videoId);
        info.GetUrlForQuality(Quality.medium);

        mainActivity.Showdialog(info);
    }

    public static void DownloadVideo(YoutubeInfo info, int which)
    {
        String url = info.urls.get(which);
        new DownloadFileAsync().execute(url, info.videoId);
    }

    public static void DeleteAllVideoData(String videoId)
    {
        File file = new File(GetThumbnailStoragePath(videoId));
        if (file.exists())
            file.delete();

        file = new File(GetVideoStoragePath(videoId));
        if (file.exists())
            file.delete();
    }
}
