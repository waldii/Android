package com.waldispd.homecast;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    String[] drawerListViewItems;
    Serie[] series;
    Staffel[] staffelList;

    File xmlFile = new File("\\\\192.168.1.115\\j$\\Serien\\serien.xml");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        LoadXmlFile();

        XmlParsing();
    }

    private String LoadXmlFile()
    {
        try
        {
            StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
            StrictMode.setThreadPolicy(tp);
            String user = "user:password";
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
            String path = "smb://192.168.1.115/Serien/serien.xml"; //192.168.1.115
            SmbFile sFile = new SmbFile(path, auth);
            InputStream smbIS = sFile.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(smbIS));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null)
            {
                total.append(line);
            }
            String temp = total.toString();
            return total.toString();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void XmlParsing()
    {
        // xml parsing
        XmlPullParserFactory pullParserFactory ;
        XmlPullParser parser = null;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();

            XmlResourceParser xrp = getApplicationContext().getResources().getXml(R.xml.test);

            parseXML(xrp);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void parseXML(XmlResourceParser parser)
    {
        try {
            ArrayList<Serie> series = new ArrayList();
            int eventType = parser.getEventType();
            Serie curSerie = null;
            Staffel curStaffel = null;
            Episode curEpisode = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        //series = new ArrayList();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("serie")) {
                            curSerie = new Serie();
                            curSerie.titel = parser.getAttributeValue(null, "name");
                        }
                        else if (name.equals("staffel"))
                        {
                            curStaffel = new Staffel();
                            curStaffel.number = Integer.parseInt(parser.getAttributeValue(null, "nr"));
                        }
                        else if (name.equals("episode"))
                        {
                            curEpisode = new Episode();
                            curEpisode.number = Integer.parseInt(parser.getAttributeValue(null, "nr"));
                            curEpisode.path = parser.getAttributeValue(null, "path");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("serie"))
                        {
                            series.add(curSerie);
                        }
                        else if (name.equals("staffel"))
                        {
                            curSerie.staffelList.add(curStaffel);
                        }
                        else if (name.equals("episode"))
                        {
                            curStaffel.episodeList.add(curEpisode);
                        }
                }
                eventType = parser.next();
            }

            printSeries(series);
        }
        catch (Exception ex)
        {
            int b = 1 + 2;
        }
    }

    private void printSeries(ArrayList<Serie> seriesList)
    {
        series = seriesList.toArray(new Serie[seriesList.size()]);
        ListView drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerListViewItems = new String[series.length];
        for (int i = 0; i < series.length; i++)
        {
            drawerListViewItems[i] = series[i].titel;
        }
        drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_listview_item, drawerListViewItems));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        try
        {
            LoadStaffelFragment(series[position]);
        }
        catch (Exception ex)
        {
            return;
        }
    }

    public void LoadEpisode(Episode episode)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Episode ::" + String.valueOf(episode.number), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void LoadStaffelFragment(Serie serie)
    {
        StaffelFragment newFragment = new StaffelFragment();
        newFragment.mSerie = serie;
        newFragment.mActivity = this;
        newFragment.setArguments(getIntent().getExtras());
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        //transaction.addToBackStack("Staffel");
        transaction.commit();
    }

    public void LoadEpisodeFragment(Staffel staffel)
    {
        EpisodeFragment newFragment = new EpisodeFragment();
        newFragment.mStaffel = staffel;
        newFragment.mActivity = this;
        newFragment.setArguments(getIntent().getExtras());
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        //transaction.addToBackStack();
        transaction.commit();
    }

    public void onSectionAttached(int number) {

        mTitle = drawerListViewItems[number-1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends android.app.Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
