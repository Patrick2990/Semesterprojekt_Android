package com.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private Context context;

    public AudioPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    public void playAudioFromRaw(int raw) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("sound", true)) {
            try {
                Uri rawPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + raw);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, rawPath);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                Log.e("SHOOTER", "FAILED IN AUDIOPLAYER " + context.getResources().getResourceName(raw));
            }
        }
    }
}