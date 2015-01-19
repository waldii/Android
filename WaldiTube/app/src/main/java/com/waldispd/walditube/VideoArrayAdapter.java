package com.waldispd.walditube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        TextView textViewTitle = (TextView) rawView.findViewById(R.id.title);
        textViewTitle.setText(values[position].title);

        TextView textViewDescription = (TextView) rawView.findViewById(R.id.description);
        textViewDescription.setText(values[position].description);

        ImageView thumbnail = (ImageView) rawView.findViewById(R.id.thumbnail);

        ThumbnailLoadingAsyncTask asyncTask = new ThumbnailLoadingAsyncTask(values[position].videoId, thumbnail);
        asyncTask.execute();


        return rawView;
    }

    private class ThumbnailLoadingAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private String videoId = "";
        private ImageView imageView;

        public ThumbnailLoadingAsyncTask(String videoId, ImageView imageView)
        {
            this.videoId = videoId;
            this.imageView = imageView;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Bitmap bb = DownloadThumbnail(Util.GetThumbnailImageUrl(videoId));
            Bitmap bMap = BitmapFactory.decodeFile(Util.GetThumbnailStoragePath(videoId));
            Util.mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bb);
                }
            });

            return null;
        }
    }

    private Bitmap DownloadThumbnail(String stringUrl)
    {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            FileInputStream in;
            BufferedInputStream buf;
            try {
                //in = new FileInputStream("/sdcard/test2.png");
                buf = new BufferedInputStream(urlConnection.getInputStream());
                return BitmapFactory.decodeStream(buf);
            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
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
