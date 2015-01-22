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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by waldispd on 19.01.2015.
 */
public final class VideoArrayAdapter extends ArrayAdapter<YoutubeVideo>
{
    private final Context context;
    private final ArrayList<YoutubeVideo> values;

    public VideoArrayAdapter(Context context, ArrayList<YoutubeVideo> values)
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

        YoutubeVideo video = values.get(position);

        TextView textViewTitle = (TextView) rawView.findViewById(R.id.title);
        textViewTitle.setText(video.title);

        TextView textViewDescription = (TextView) rawView.findViewById(R.id.description);
        textViewDescription.setText(video.description);

        TextView textViewDuration = (TextView) rawView.findViewById(R.id.duration);
        textViewDuration.setText(Util.GetDurationString(video.duration));

        ImageView thumbnail = (ImageView) rawView.findViewById(R.id.thumbnail);

        if (new File(Util.GetThumbnailStoragePath(video.videoId)).exists())
        {
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;*/
            Bitmap bitmap = BitmapFactory.decodeFile(Util.GetThumbnailStoragePath(video.videoId));
            thumbnail.setImageBitmap(bitmap);
        }
        else
        {
            ThumbnailLoadingAsyncTask asyncTask = new ThumbnailLoadingAsyncTask(video.videoId, thumbnail, video.favorited);
            asyncTask.execute();
        }
        return rawView;
    }

    private class ThumbnailLoadingAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private String videoId = "";
        private ImageView imageView;
        private boolean favorited = false;

        public ThumbnailLoadingAsyncTask(String videoId, ImageView imageView, boolean favorited)
        {
            this.videoId = videoId;
            this.imageView = imageView;
            this.favorited = favorited;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Bitmap bb = DownloadThumbnail(Util.GetThumbnailImageUrl(videoId));
            Bitmap bMap = BitmapFactory.decodeFile(Util.GetThumbnailStoragePath(videoId));
            if (favorited)
            {
                SafeBmpToSdCard(bMap);
            }
            Util.mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bb);
                }
            });

            return null;
        }

        private void SafeBmpToSdCard(Bitmap bitmap)
        {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(Util.GetThumbnailStoragePath(videoId));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


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
