package com.waldispd.walditube;

import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Dominik on 19.01.2015.
 */
public class YoutubeInfo
{
    ArrayList<Pair<String, String>> keyValuePairs = new ArrayList<>();
    String text;
    String videoId;
    int duration = 0;
    int qualityIndex = 0;
    ArrayList<String> qualitys = new ArrayList<>();
    int urlsIndex = 0;
    ArrayList<String> urls = new ArrayList<>();

    public YoutubeInfo(String text, String videoId)
    {
        this.videoId = videoId;
        this.text = text;
        keyValuePairs = new ArrayList<>();
        String txt;
        String key;
        String value;
        for (int i = 0; i < text.split("&").length; i++) {
            txt = text.split("&")[i];
            int index = txt.indexOf("=");
            key = txt.substring(0, index);
            value = txt.substring(index + 1, txt.length());
            keyValuePairs.add(new Pair<String, String>(key, value));
        }



    }

    public String GetUrlForQuality(Quality quality) throws UnsupportedEncodingException {
        /*int index = keyValuePairs.indexOf(new Pair<String, String>("quality", "medium"));
        ArrayList<Pair<String, String>> keyValuePairsTmp = new ArrayList<>();
        keyValuePairsTmp.addAll(keyValuePairs.subList(index, keyValuePairs.size() - 1));
        for (int i = 0; i < keyValuePairsTmp.size(); i++) {
            if (keyValuePairsTmp.get(i).first.contains("url"))
            {
                String url = keyValuePairsTmp.get(i).second;
                return URLDecoder.decode(url, "UTF-8");
            }
        }*/

        String[] tokens = text.split("&");
        int index = 0;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == null) {
                continue;
            }
            String param1 = tokens[i].substring(0, tokens[i].indexOf("="));
            //String param2 = tokens[i].substring(param1.length() + 1);
            if (param1.contains("quality")) {
                index = i - 1;
                break;
            }
        }

        String[] finalTokens = new String[tokens.length - index];
        for (int i = 0; i < tokens.length - index; i++) {
            finalTokens[i] = tokens[i + index - 1];
        }

        for (int i = 0; i < finalTokens.length; i++) {
            String param1 = finalTokens[i].substring(0, finalTokens[i].indexOf("="));
            String param2 = finalTokens[i].substring(param1.length() + 1);
            if (param1.contains("quality")) {
                qualitys.add(param2);
                qualityIndex++;
            } else if (param1.contains("url")) {
                urls.add(param2);
                urlsIndex++;
            }
        }
        int b = 1 + 1;

        return null;
    }
}

enum Quality
{
    medium,
    hd720,
    small
}
