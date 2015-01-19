package com.waldispd.walditube;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dominik on 19.01.2015.
 */
public class DownloadAsyncTask extends AsyncTask<Void, Void, Void>
{
    public String text;
    private String url;

    public DownloadAsyncTask(String url)
    {
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        text =  DownloadSearchResults(url);
        return null;
    }

    private String DownloadSearchResults(String stringUrl)
    {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                return total.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}