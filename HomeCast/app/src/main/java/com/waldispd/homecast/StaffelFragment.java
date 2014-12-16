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
 * Created by waldispd on 15.12.2014.
 */
public class StaffelFragment extends Fragment
{
    public View mView;
    public Serie mSerie;
    public MainActivity mActivity;

    public StaffelFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staffel, container, false);
        mView = view;

        Staffel[] staffelList = mSerie.staffelList.toArray(new Staffel[mSerie.staffelList.size()]);
        String[] staffelTitel = new String[staffelList.length];
        for (int i = 0; i < staffelList.length; i++) {
            staffelTitel[i] = "Staffel " + staffelList[i].number;
        }
        ListView staffelListView = (ListView) view.findViewById(R.id.staffelList);
        staffelListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                int b = 1 + 2;
            }
        });
        staffelListView.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.drawer_listview_item, staffelTitel));
        return view;
    }
}
