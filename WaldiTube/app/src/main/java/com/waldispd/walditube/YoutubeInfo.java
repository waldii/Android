package com.waldispd.walditube;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by Dominik on 19.01.2015.
 */
public class YoutubeInfo
{
    ArrayList<Pair<String, String>> keyValuePairs = new ArrayList<>();

    public YoutubeInfo(String text)
    {
        keyValuePairs = new ArrayList<>();
        String txt;
        String key;
        String value;
        for (int i = 0; i < text.split("&").length; i++) {
            txt = text.split("&")[i];
            int index = txt.indexOf("=");
            key = txt.substring(0, index);
            value = txt.substring(index, txt.length()-1);
            keyValuePairs.add(new Pair<String, String>(key, value));
        }
    }

    public String GetUrlForQuality(Quality quality)
    {
        int index = keyValuePairs.indexOf(new Pair<String, String>("quality", quality.toString()));
        ArrayList<Pair<String, String>> keyValuePairsTmp = new ArrayList<>();
        keyValuePairsTmp.addAll(keyValuePairs.subList(index, keyValuePairs.size() - 1));
        for (int i = 0; i < keyValuePairsTmp.size(); i++) {
            if (keyValuePairsTmp.get(i).first == "url")
            {
                return keyValuePairsTmp.get(i).second;
            }
        }
        return null;
    }
}

enum Quality
{
    medium,
    hd720,
    small
}
