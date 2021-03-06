package com.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.services.MusicService;

public class BaseActivity extends AppCompatActivity {

    boolean musicIsBound = false;
    static MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind MusicService
        doBindService();
    }

    @Override
    protected void onDestroy() {
        // unBind MusicService
        doUnbindService();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        doUnbindService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        doBindService();
        super.onPostResume();
    }

    // Create ServiceConnection to MusicService
    ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            musicService = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    // Bind MusicService method
    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                serviceConnection, BIND_AUTO_CREATE);
        musicIsBound = true;
    }

    // Unbind MusicService method
    void doUnbindService() {
        if (musicIsBound) {
            unbindService(serviceConnection);
            musicIsBound = false;
        }
    }
}