package com.waldispd.walditube;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dominik on 19.01.2015.
 */
public class DownloadFileAsync extends AsyncTask<String, String, String> {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 1;
    private ProgressDialog progressDialog;

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        int count;

        try {

            URL url = new URL(params[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lengthofFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Length of file: " + lengthofFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(Util.GetVideoStoragePath(params[1]));

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthofFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            //convert
            //ExecuteFFmpegCommand("-i sdcard/Download/video2.mp4 -vn -strict -2 sdcard/Download/video2.m4a");

        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        Util.mainActivity.dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        Util.mainActivity.showDialog(DIALOG_DOWNLOAD_PROGRESS);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        Log.d("ANDRO_ASYNC", values[0]);
        progressDialog.setProgress(Integer.parseInt(values[0]));
    }

    public class MainActiity extends ActionBarActivity
    {
        @Override
        protected Dialog onCreateDialog(int id) {
            // TODO Auto-generated method stub
            switch (id) {
                case DIALOG_DOWNLOAD_PROGRESS:
                    progressDialog = new ProgressDialog(Util.mainActivity.getApplicationContext());
                    progressDialog.setMessage("Downloading file...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    return progressDialog;
                default:
                    return null;
            }
        }
    }



}