package com.waldispd.walditube;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.waldispd.walditube.Enums.RepeatMode;

import java.io.IOException;
import java.util.Random;

/**
 * Created by waldispd on 23.01.2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener
{
    private final IBinder musicBind = new MusicBinder();

    View playerview;
    final int NOTIFY_ID = 1;
    public MediaPlayer mediaPlayer;
    YoutubeVideo video;
    RepeatMode repeatMode = RepeatMode.RepeatAll;
    Handler mHandler = new Handler();
    TextView titleText;
    SeekBar progressBar;
    ImageView thumbnail;
    TextView curDuration;
    TextView fullDuration;
    boolean shufflePlay = false;

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();
        stopForeground(true);
        return false;
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    public void initMusicPlayer(View playerView)
    {
        playerview = playerView;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);


        titleText = (TextView)playerview.findViewById(R.id.textViewSongtitle);
        progressBar = (SeekBar)playerview.findViewById(R.id.seekBarSongProgress);
        progressBar.setOnSeekBarChangeListener(this);
        thumbnail = (ImageView)playerview.findViewById(R.id.imageViewSongThumbnail);
        curDuration = (TextView)playerView.findViewById(R.id.textViewCurrentDuration);
        fullDuration = (TextView)playerView.findViewById(R.id.textViewFullDuration);
        ImageButton randomButton = (ImageButton)playerview.findViewById(R.id.imageButtonSongRandom);
        ImageButton previousButton = (ImageButton)playerview.findViewById(R.id.imageButtonSongPrevious);
        ImageButton playButton = (ImageButton)playerview.findViewById(R.id.imageButtonSongPlay);
        ImageButton nextButton = (ImageButton)playerview.findViewById(R.id.imageButtonSongNext);
        ImageButton repeatButton = (ImageButton)playerview.findViewById(R.id.imageButtonSongRepeat);

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shufflePlay = false;
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevious();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                else
                    mediaPlayer.start();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (repeatMode) {
                    case None:
                        repeatMode = RepeatMode.RepeatAll;
                        break;
                    case RepeatAll:
                        repeatMode = RepeatMode.RepeatSong;
                        break;
                    case RepeatSong:
                        repeatMode = RepeatMode.None;
                        break;
                }
            }
        });
    }

    public void PlaySong(YoutubeVideo yVideo)
    {
        Util.mainActivity.getSupportActionBar().setSelectedNavigationItem(1);
        try {
            this.video = yVideo;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Util.GetAudioStoragePath(yVideo.videoId));
            mediaPlayer.prepare();
            mediaPlayer.start();
            titleText.setText(video.title);

            Bitmap bitmap = Util.GetThumbnail(video.videoId);
            thumbnail.setImageBitmap(bitmap);

            progressBar.setProgress(0);
            progressBar.setMax(100);
            updateProgressBar();

            Intent notIntent = new Intent(Util.mainActivity, MainActivity.class);
            notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendInt = PendingIntent.getActivity(Util.mainActivity, 0,
                    notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(Util.mainActivity);

            builder.setContentIntent(pendInt)
                    .setSmallIcon(R.drawable.icon)
                    .setTicker(video.title)
                    .setOngoing(true)
                    .setContentTitle("playing this: ")
                    .setContentText("Waldi-Tube");
            Notification not = builder.build();

            startForeground(NOTIFY_ID, not);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playRandom()
    {
        Random random = new Random();
        int next = random.nextInt(DataHandler.favoritedVideos.size() - 1);
        PlaySong(DataHandler.favoritedVideos.get(next));
    }

    private void playPrevious()
    {
        int index = DataHandler.favoritedVideos.indexOf(video);
        if (index == 0)
        {
            PlaySong(DataHandler.favoritedVideos.get(DataHandler.favoritedVideos.size() - 1));
        }
        else
        {
            PlaySong(DataHandler.favoritedVideos.get(index - 1));
        }
    }

    private void playNext()
    {
        int index = DataHandler.favoritedVideos.indexOf(video);
        if (index == DataHandler.favoritedVideos.size() - 1)
        {
            PlaySong(DataHandler.favoritedVideos.get(0));
        }
        else
        {
            PlaySong(DataHandler.favoritedVideos.get(index + 1));
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        if (shufflePlay)
        {
            playRandom();
            return;
        }
        switch (repeatMode)
        {
            case None:
                break;
            case RepeatAll:
                playNext();
                break;
            case RepeatSong:
                PlaySong(video);
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = Util.ProgressToTimer(seekBar.getProgress(), totalDuration);

        mediaPlayer.seekTo(currentPosition);

        updateProgressBar();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            fullDuration.setText(Util.GetDurationString((int)totalDuration / 1000));
            curDuration.setText(Util.GetDurationString((int)currentDuration / 1000));

            int progress = (int)(Util.GetProgressPercentage(currentDuration, totalDuration));
            progressBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
