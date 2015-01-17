package com.waldispd.walditube;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by waldispd on 16.01.2015.
 */
public class FavoritesFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.testlistview);

        VideoView videoview = new VideoView(getActivity().getApplicationContext());

        scrollView.addView(videoview);
        return view;
    }
}
