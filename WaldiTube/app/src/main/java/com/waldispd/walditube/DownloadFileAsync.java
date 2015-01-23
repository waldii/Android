package com.waldispd.walditube;

import android.os.AsyncTask;
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

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        int count;

        try {

            URL url = new URL(Util.GetUft8EncodedString(params[0]));
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
        Util.mainActivity.dismissDialog(Util.mainActivity.DIALOG_DOWNLOAD_PROGRESS);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        Util.mainActivity.showDialog(Util.mainActivity.DIALOG_DOWNLOAD_PROGRESS);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        Log.d("ANDRO_ASYNC", values[0]);
        Util.mainActivity.progressDialog.setProgress(Integer.parseInt(values[0]));
    }
}