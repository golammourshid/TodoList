package edu.univdhaka.cse2216.myplane.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        final MediaPlayer mediaPlayer=MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        mediaPlayer.stop();
                    }
                },
                2000
        );
    }
}
