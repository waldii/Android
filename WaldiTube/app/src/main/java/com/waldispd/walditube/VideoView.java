package com.waldispd.walditube;

import android.content.Context;
import android.content.res.TypedArray;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by waldispd on 16.01.2015.
 */
public class VideoView extends LinearLayout
{
    public VideoView(Context context)
    {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VideoView);



        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.VideoView_titel:
                    String titel = a.getString(attr);
                    //TextView view = (TextView)findViewById(R.id.Test1);
                    //view.setText(titel);
                    break;
                case R.styleable.VideoView_description:
                    String description = a.getString(attr);

                    break;
                case R.styleable.VideoView_cached:
                    Boolean cached = a.getBoolean(attr, false);

                    break;
            }
        }
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.videoview, this, true);
    }
}
