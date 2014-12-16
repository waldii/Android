package com.waldispd.homecast;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by waldispd on 16.12.2014.
 */
public class EpisodeFragment extends Fragment
{
    public Staffel mStaffel;
    public MainActivity mActivity;

    public EpisodeFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode, container, false);

        final Episode[] episodeList = mStaffel.episodeList.toArray(new Episode[mStaffel.episodeList.size()]);
        String[] episodeTitel = new String[episodeList.length];
        for (int i = 0; i < episodeList.length; i++) {
            episodeTitel[i] = "Episode " + episodeList[i].number;
        }
        ListView episodeListView = (ListView) view.findViewById(R.id.episodeList);
        episodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                mActivity.LoadEpisode(episodeList[position]);
            }
        });
        episodeListView.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.drawer_listview_item, episodeTitel));

        return view;
    }
}
