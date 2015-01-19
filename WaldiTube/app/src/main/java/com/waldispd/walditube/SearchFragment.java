package com.waldispd.walditube;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by waldispd on 16.01.2015.
 */
public class SearchFragment extends Fragment
{
    final Context applicationContext;
    View view;

    public SearchFragment(Context applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchOnYoutube();
            }
        });

        return view;
    }

    private void SearchOnYoutube()
    {
        final EditText searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        String searchTerm = searchEditText.getText().toString();

        ArrayList<YoutubeVideo> list = new ArrayList<>();

        try {
            DownloadAsyncTask task = new DownloadAsyncTask(GetSearchUrl(searchTerm));
            task.execute();
            task.get();
            list = XmlParsing(task.text);

            // Set listivew
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private class DownloadAsyncTask extends AsyncTask<Void, Void, Void>
    {
        public String text = "";
        private String url = "";

        public DownloadAsyncTask(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            text =  DownloadSearchResults(url);
            return null;
        }
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

    final String searchUrl = "https://gdata.youtube.com/feeds/api/videos?q=";

    public String GetSearchUrl(String term)
    {
        return searchUrl + term;
    }

    public ArrayList<YoutubeVideo> XmlParsing(String string)
    {
        // xml parsing
        XmlPullParserFactory pullParserFactory ;
        XmlPullParser parser = null;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            parser = pullParserFactory.newPullParser();
            //XmlResourceParser xrp = getApplicationContext().getResources().getXml(R.xml.test);
            parser.setInput(new StringReader(string));
            return ParseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<YoutubeVideo> ParseXML(XmlPullParser parser) {
        try {
            ArrayList<YoutubeVideo> searchVideos = new ArrayList();
            YoutubeVideo curVideo = null;
            int eventType = parser.getEventType();
            Boolean headerIsOver = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        //series = new ArrayList();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("entry")) {
                            curVideo = new YoutubeVideo();
                        } else if (name.equals("id")) {
                            if (!headerIsOver)
                                break;
                            curVideo.videoId = parser.nextText();
                        } else if (name.equals("title")) {
                            if (!headerIsOver)
                                break;
                            curVideo.title = parser.nextText();
                        } else if (name.equals("content")) {
                            if (!headerIsOver)
                                break;
                            curVideo.description = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("entry")) {
                            searchVideos.add(curVideo);
                        } else if (name.equals("generator")){
                            headerIsOver = true;
                        }

                }
                eventType = parser.next();
            }

            return searchVideos;
        } catch (Exception ex) {
            int b = 1 + 2;
        }
        return null;
    }
}
