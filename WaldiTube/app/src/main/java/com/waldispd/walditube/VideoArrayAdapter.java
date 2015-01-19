package com.waldispd.walditube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by waldispd on 19.01.2015.
 */
public final class VideoArrayAdapter extends ArrayAdapter<YoutubeVideo>
{
    private final Context context;
    private final YoutubeVideo[] values;

    public VideoArrayAdapter(Context context, YoutubeVideo[] values)
    {
        super(context, R.layout.videoview, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rawView = inflater.inflate(R.layout.videoview, parent, false);
        TextView textView = (TextView) rawView.findViewById(R.id.title);
        TextView textView2 = (TextView) rawView.findViewById(R.id.description);
        textView.setText(values[position].title);
        textView2.setText(values[position].description);
        return rawView;
    }
}
