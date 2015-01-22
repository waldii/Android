package com.waldispd.walditube;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Created by Dominik on 19.01.2015.
 */
public class DataHandler
{
    public static ArrayList<YoutubeVideo> favoritedVideos = new ArrayList<>();

    public static void AddFavoritedVideo(YoutubeVideo video) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
        for (int i = 0; i < favoritedVideos.size(); i++) {
            if (favoritedVideos.get(i).videoId.toLowerCase() == video.videoId.toLowerCase()){
                Util.MakeToast("allready one fav-video with id : " + video.videoId, false);
                return;
            }
        }
        Util.DownloadAllVideoData(video.videoId);
        favoritedVideos.add(video);
        try {
            SerializeObjectData();
            Util.MakeToast("successfully added", false);
        } catch (IOException e) {
            Util.MakeToast("failed to add", false);
            e.printStackTrace();
        }
    }

    public static void RemoveFavoritedVideo(YoutubeVideo video)
    {
        Util.DeleteAllVideoData(video.videoId);
        favoritedVideos.remove(video);
        try {
            SerializeObjectData();
            Util.MakeToast("successfully removed", false);
        } catch (IOException e) {
            Util.MakeToast("failed to remove", false);
            e.printStackTrace();
        }
    }

    private static void SerializeObjectData() throws IOException
    {
        Writer writer = new BufferedWriter( new FileWriter( new File("/sdcard/walditube/favorites.json") ) );
        try{
            JSONSerializer serializer = new JSONSerializer();
            serializer.deepSerialize(favoritedVideos, writer);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public static void DeserializeObjectData()
    {
        String ret = "";

        try {
            InputStream inputStream = new FileInputStream( new File("/sdcard/walditube/favorites.json"));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                ArrayList<YoutubeVideo> response = new JSONDeserializer<ArrayList<YoutubeVideo>>().deserialize(ret, ArrayList.class);
                favoritedVideos = response;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
