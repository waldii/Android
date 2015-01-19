package com.waldispd.walditube;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener
{
    SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<Pair<String, Fragment>> Tabs = new ArrayList<>();

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        FavoritesFragment favFragment = new FavoritesFragment(getApplicationContext());
        Bundle args = new Bundle();
        args.putInt("section_number", 0);
        favFragment.setArguments(args);
        Tabs.add(new Pair<String, Fragment>("Favorites", favFragment));

        PlayerFragment playFragment = new PlayerFragment();
        Bundle args2 = new Bundle();
        args.putInt("section_number", 1);
        playFragment.setArguments(args2);
        Tabs.add(new Pair<String, Fragment>("Player", playFragment));

        SearchFragment searchFragment = new SearchFragment(getApplicationContext());
        Bundle args3 = new Bundle();
        args.putInt("section_number", 2);
        searchFragment.setArguments(args3);
        Tabs.add(new Pair<String, Fragment>("Search", searchFragment));

        for (int i = 0; i < Tabs.size(); i++)
        {
            actionBar.addTab(actionBar.newTab().setText(Tabs.get(i).first).setTabListener(this));
        }



        //InitFFMpeg();
        //startDownload();
    }

    private void InitFFMpeg()
    {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    private void ExecuteFFmpegCommand(String cmd)
    {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onProgress(String message) {}

                @Override
                public void onFailure(String message) {}

                @Override
                public void onSuccess(String message) {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    public static final int DIALOG_DOWNLOAD_PROGRESS = 1;
    private ProgressDialog progressDialog;

    private void startDownload()
    {
        String url = "http://r16---sn-ab5l6nee.googlevideo.com/videoplayback?key=yt5&upn=ndXgfRmsSVY&dur=31.973&id=o-ALYvUwSwjsJMRIRX524qZSvjzSb0w3H0EcRPgGyiDsG3&itag=18&pl=24&mt=1421412453&ratebypass=yes&fexp=900359,900718,907263,927622,9405841,941004,943917,947209,947225,948124,952302,952605,952901,955301,957103,957105,957201,959701&ms=au&expire=1421434152&mv=m&source=youtube&mm=31&sparams=dur,id,ip,ipbits,itag,mm,ms,mv,pl,ratebypass,source,upn,expire&sver=3&ipbits=0&signature=D975D4E462611A5135610A6BE3DA7652D5DE28A3.C83074C244A8D3CE3F6BB4ECA8EAA48C9AAD9C81&ip=196.3.50.254";
        new DownloadFileAsync().execute(url);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Downloading file...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.show();
                return progressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

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
                OutputStream output = new FileOutputStream("sdcard/Download/video2.mp4");

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
                ExecuteFFmpegCommand("-i sdcard/Download/video2.mp4 -vn -strict -2 sdcard/Download/video2.m4a");

            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            Log.d("ANDRO_ASYNC", values[0]);
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return Tabs.get(position).second;
        }

        @Override
        public int getCount() {
            return 3;//Tabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return Tabs.get(position).first.toUpperCase(l);
            /*switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;*/
        }
    }


}