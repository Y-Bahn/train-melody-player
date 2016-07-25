package io.ybahn.trainmelody;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final String ACTION_PLAY = "io.ybahn.trainmelody.service.ACTION_PLAY";
    public static final String ACTION_STOP = "io.ybahn.trainmelody.service.ACTION_STOP";
    private MediaPlayer mediaPlayer = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_PLAY)) {
            if(mediaPlayer.isPlaying()) {
                return START_STICKY;
            }
            Bundle bundle = intent.getExtras();
            String path = bundle.getString("path");
            String title = bundle.getString("title");

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);

            Intent startIntentToWidget = new Intent();
            startIntentToWidget.setAction(NewAppWidget.ACTION_PLAY);
            startIntentToWidget.putExtra("title", title);
            getBaseContext().sendBroadcast(startIntentToWidget);

            Intent startIntentToMainActivity = new Intent();
            startIntentToMainActivity.setAction(MainActivity.ACTION_PLAY);
            getBaseContext().sendBroadcast(startIntentToMainActivity);

            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync(); //prepare async to not block main thread
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(intent.getAction().equals(ACTION_STOP)) {

            if (mediaPlayer.isPlaying()) {
                sendStopIntentToWidget();
                sendStopIntentToMainActivity();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendStopIntentToMainActivity();
        sendStopIntentToWidget();
        stopSelf();
    }
    private void sendStopIntentToMainActivity() {
        Intent stopIntent = new Intent();
        stopIntent.setAction(MainActivity.ACTION_STOP);
        getBaseContext().sendBroadcast(stopIntent);
    }
    private void sendStopIntentToWidget() {
        Intent stopIntent = new Intent();
        stopIntent.setAction(NewAppWidget.ACTION_STOP);
        getBaseContext().sendBroadcast(stopIntent);
    }
}
