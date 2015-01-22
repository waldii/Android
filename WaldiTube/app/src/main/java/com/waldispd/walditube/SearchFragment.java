package com.waldispd.walditube;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by waldispd on 16.01.2015.
 */
public class SearchFragment extends Fragment
{
    final Context applicationContext;
    VideoArrayAdapter videoArrayAdapter = null;
    View view;
    ArrayList<YoutubeVideo> videosList = new ArrayList<>();

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

        ListView listView = (ListView)view.findViewById(R.id.searchListView);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                YoutubeVideo video = videosList.get(position);
                try {
                    video.ToFavorited();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return false;
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
            DownloadAsyncTask task = new DownloadAsyncTask(Util.GetSearchUrl(searchTerm));
            task.execute();
            task.get();
            videosList = XmlParsing(task.text);
            //videos = videosList.toArray(new YoutubeVideo[videosList.size()]);

            // Set listivew
            ListView listView = (ListView)view.findViewById(R.id.searchListView);
            videoArrayAdapter = new VideoArrayAdapter(applicationContext, videosList);
            listView.setAdapter(videoArrayAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // XML stuff    ----
    public ArrayList<YoutubeVideo> XmlParsing(String string)
    {
        // xml parsing
        XmlPullParserFactory pullParserFactory ;
        XmlPullParser parser = null;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            parser = pullParserFactory.newPullParser();

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
                            curVideo.videoId = Util.GetVideoIdFromLink(parser.nextText());
                        } else if (name.equals("title")) {
                            if (!headerIsOver)
                                break;
                            curVideo.title = parser.nextText();
                        } else if (name.equals("content")) {
                            if (!headerIsOver)
                                break;
                            curVideo.description = parser.nextText();
                        } else if (name.equals("media:content")) {
                            if (!headerIsOver)
                                break;
                            curVideo.duration = Integer.parseInt(parser.getAttributeValue(null, "duration"));
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
