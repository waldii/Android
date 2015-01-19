package com.waldispd.walditube;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by waldispd on 16.01.2015.
 */
public class FavoritesFragment extends Fragment
{
    final Context applicationContext;
    public FavoritesFragment(Context applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        YoutubeVideo[] videos = new YoutubeVideo[3];
        videos[0] = new YoutubeVideo("test1.1", "test1.2");
        videos[1] = new YoutubeVideo("test2.1", "test2.2");
        videos[2] = new YoutubeVideo("test3.1", "test3.2");

        ArrayList<YoutubeVideo> list = new ArrayList<YoutubeVideo>();
        list.add(new YoutubeVideo("test1", "test2"));
        list.add(new YoutubeVideo("test12", "test22"));
        list.add(new YoutubeVideo("test13", "test23"));

        ListView listView = (ListView)view.findViewById(R.id.favoritesListView);
        final VideoArrayAdapter videoArrayAdapter = new VideoArrayAdapter(applicationContext, videos);
        listView.setAdapter(videoArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(applicationContext,
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

        return view;
    }
}
