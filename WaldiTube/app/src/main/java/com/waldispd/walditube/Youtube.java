package com.waldispd.walditube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by waldispd on 20.01.2015.
 */
public class Youtube {
    /*
     * this function downloads a video based on its url
     */
    public static void download(String url, String fileName) throws Exception {
        String id = url.split("v=")[1].split("&")[0];
        System.out.println(url + " " + id);
        String nid = getDownloadId(id);
        if (nid == null) {
            throw new Exception("Invalud url");
        }
        url = "http://www.youtube.com/get_video?video_id=" + id + "&t=" + nid;
        downloadStreamData(url, fileName);
    }

    /*
     * returns the id of the video data
     */
    private static String getDownloadId(String videoId) throws MalformedURLException, IOException {
        String url = "http://www.youtube.com/get_video_info?&video_id=" + videoId;
        URL tU = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) tU.openConnection();
        InputStream ins = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(ins));
        String line;
        StringBuffer content = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            content.append(line);
        }
        String sContent = URLDecoder.decode(content.toString(), "UTF-8");
        String[] tokens = sContent.split("&");
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i] == null) {
                continue;
            }
            String param1 = tokens[i].substring(0, tokens[i].indexOf("="));
            String param2 = tokens[i].substring(param1.length() + 1);
            if (param1.equals("token")) {
                return param2;
            }
        }
        return "";
    }

    /*
     * this function will download the file after the stream is found
     */
    private static void downloadStreamData(String url, String fileName) throws Exception {
        URL tU = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) tU.openConnection();

        String type = conn.getContentType();
        InputStream ins = conn.getInputStream();
        FileOutputStream fout = new FileOutputStream(new File(fileName));
        byte[] outputByte = new byte[4096];
        int bytesRead;
        int length = conn.getContentLength();
        int read = 0;
        while ((bytesRead = ins.read(outputByte, 0, 4096)) != -1) {
            read += bytesRead;
            System.out.println(read + " out of " + length);
            fout.write(outputByte, 0, bytesRead);
        }
        fout.flush();
        fout.close();
    }

}