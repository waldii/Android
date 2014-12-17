package com.waldispd.homecast;

import android.app.Activity;
import android.app.MediaRouteActionProvider;
import android.content.res.XmlResourceParser;
//import android.media.MediaRouter;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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

    // ChromeCast \\
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private RemoteMediaPlayer mRemoteMediaPlayer;
    private Cast.Listener mCastClientListener;
    private boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;
    private boolean mVideoIsLoaded;
    private boolean mIsPlaying;

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

        InitMediaRouter();
    }

    private void InitMediaRouter()
    {
        mMediaRouter = android.support.v7.media.MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getString(R.string.application_id)))
                .build();
        mMediaRouterCallback = new MediaRouterCallback();
    }

    private String LoadXmlFile()
    {
        try
        {
            StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
            StrictMode.setThreadPolicy(tp);
            String user = "user:pw";
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
        if( !mVideoIsLoaded )
            startVideo(episode);
        else
            controlVideo();
    }

    public void startVideo(Episode episode)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Episode ::" + String.valueOf(episode.number), Toast.LENGTH_SHORT);
        toast.show();

        MediaMetadata mediaMetadata = new MediaMetadata( MediaMetadata.MEDIA_TYPE_MOVIE );
        mediaMetadata.putString( MediaMetadata.KEY_TITLE, getString(episode.number) );

        MediaInfo mediaInfo = new MediaInfo.Builder( episode.path)
                .setContentType( getString( R.string.content_type_mp4 ) )
                .setStreamType( MediaInfo.STREAM_TYPE_BUFFERED )
                .setMetadata( mediaMetadata )
                .build();
        try {
            mRemoteMediaPlayer.load( mApiClient, mediaInfo, true )
                    .setResultCallback( new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult( RemoteMediaPlayer.MediaChannelResult mediaChannelResult ) {
                            if( mediaChannelResult.getStatus().isSuccess() ) {
                                mVideoIsLoaded = true;
                                //TODO: mButton.setText( getString( R.string.pause_video ) );
                            }
                        }
                    } );
        } catch( Exception e ) {
        }
    }

    private void controlVideo() {
        if( mRemoteMediaPlayer == null || !mVideoIsLoaded )
            return;

        if( mIsPlaying ) {
            mRemoteMediaPlayer.pause( mApiClient );
            //mButton.setText( getString( R.string.resume_video ) );
        } else {
            mRemoteMediaPlayer.play( mApiClient );
            //mButton.setText( getString( R.string.pause_video ) );
        }
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
            MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
            android.support.v7.app.MediaRouteActionProvider mediaRouteActionProvider  = (android.support.v7.app.MediaRouteActionProvider)
                    MenuItemCompat.getActionProvider(mediaRouteMenuItem);
            mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Start media router discovery
        mMediaRouter.addCallback( mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN );
    }

    @Override
    protected void onPause() {
        if ( isFinishing() ) {
            // End media router discovery
            mMediaRouter.removeCallback( mMediaRouterCallback );
        }
        super.onPause();
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

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            initCastClientListener();
            initRemoteMediaPlayer();

            mSelectedDevice = CastDevice.getFromBundle( info.getExtras() );

            launchReceiver();
        }

        @Override
        public void onRouteUnselected( MediaRouter router, MediaRouter.RouteInfo info ) {
            teardown();
            mSelectedDevice = null;
            //TODO: mButton.setText( getString( R.string.play_video ) );
            mVideoIsLoaded = false;
        }

        private void initCastClientListener() {
            mCastClientListener = new Cast.Listener() {
                @Override
                public void onApplicationStatusChanged() {
                }

                @Override
                public void onVolumeChanged() {
                }

                @Override
                public void onApplicationDisconnected( int statusCode ) {
                    teardown();
                }
            };
        }

        private void launchReceiver() {
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder( mSelectedDevice, mCastClientListener );

            ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks();
            ConnectionFailedListener mConnectionFailedListener = new ConnectionFailedListener();
            mApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi( Cast.API, apiOptionsBuilder.build() )
                    .addConnectionCallbacks( mConnectionCallbacks )
                    .addOnConnectionFailedListener( mConnectionFailedListener )
                    .build();

            mApiClient.connect();
        }

        private void initRemoteMediaPlayer() {
            mRemoteMediaPlayer = new RemoteMediaPlayer();
            mRemoteMediaPlayer.setOnStatusUpdatedListener( new RemoteMediaPlayer.OnStatusUpdatedListener() {
                @Override
                public void onStatusUpdated() {
                    MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
                    mIsPlaying = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
                }
            });

            mRemoteMediaPlayer.setOnMetadataUpdatedListener( new RemoteMediaPlayer.OnMetadataUpdatedListener() {
                @Override
                public void onMetadataUpdated() {
                }
            });
        }
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected( Bundle hint ) {
            if( mWaitingForReconnect ) {
                mWaitingForReconnect = false;
                reconnectChannels( hint );
            } else {
                try {
                    Cast.CastApi.launchApplication( mApiClient, getString( R.string.application_id ), false )
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                Cast.ApplicationConnectionResult applicationConnectionResult) {
                                            Status status = applicationConnectionResult.getStatus();
                                            if( status.isSuccess() ) {
                                                //Values that can be useful for storing/logic
                                                ApplicationMetadata applicationMetadata =
                                                        applicationConnectionResult.getApplicationMetadata();
                                                String sessionId =
                                                        applicationConnectionResult.getSessionId();
                                                String applicationStatus =
                                                        applicationConnectionResult.getApplicationStatus();
                                                boolean wasLaunched =
                                                        applicationConnectionResult.getWasLaunched();

                                                mApplicationStarted = true;
                                                reconnectChannels( null );
                                            }
                                        }
                                    }
                            );
                } catch ( Exception e ) {

                }
            }
        }

        private void reconnectChannels( Bundle hint ) {
            if( ( hint != null ) && hint.getBoolean( Cast.EXTRA_APP_NO_LONGER_RUNNING ) ) {
                //Log.e( TAG, "App is no longer running" );
                teardown();
            } else {
                try {
                    Cast.CastApi.setMessageReceivedCallbacks( mApiClient, mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer );
                } catch( IOException e ) {
                    //Log.e( TAG, "Exception while creating media channel ", e );
                } catch( NullPointerException e ) {
                    //Log.e( TAG, "Something wasn't reinitialized for reconnectChannels" );
                }
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mWaitingForReconnect = true;
        }
    }

    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed( ConnectionResult connectionResult ) {
            teardown();
        }
    }

    private void teardown() {
        if( mApiClient != null ) {
            if( mApplicationStarted ) {
                try {
                    Cast.CastApi.stopApplication( mApiClient );
                    if( mRemoteMediaPlayer != null ) {
                        Cast.CastApi.removeMessageReceivedCallbacks( mApiClient, mRemoteMediaPlayer.getNamespace() );
                        mRemoteMediaPlayer = null;
                    }
                } catch( IOException e ) {
                    //Log.e( TAG, "Exception while removing application " + e );
                }
                mApplicationStarted = false;
            }
            if( mApiClient.isConnected() )
                mApiClient.disconnect();
            mApiClient = null;
        }
        mSelectedDevice = null;
        mVideoIsLoaded = false;
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
