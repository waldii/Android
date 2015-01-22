package com.waldispd.walditube;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by waldispd on 16.01.2015.
 */
public class FavoritesFragment extends Fragment
{
    final Context applicationContext;
    YoutubeVideo[] favVideos;

    public FavoritesFragment(Context applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favVideos = DataHandler.favoritedVideos.toArray(new YoutubeVideo[DataHandler.favoritedVideos.size()]);

        final ListView listView = (ListView)view.findViewById(R.id.favoritesListView);
        ArrayList<YoutubeVideo> lst = new ArrayList<>();
        lst.addAll(Arrays.asList(favVideos));
        final VideoArrayAdapter videoArrayAdapter = new VideoArrayAdapter(applicationContext, lst);
        listView.setAdapter(videoArrayAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    YoutubeVideo video = favVideos[position];
                    videoArrayAdapter.remove(video);
                    DataHandler.RemoveFavoritedVideo(video);
                    //favVideos = DataHandler.favoritedVideos.toArray(new YoutubeVideo[DataHandler.favoritedVideos.size()]);

                    videoArrayAdapter.notifyDataSetChanged();
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        return view;
    }
}
