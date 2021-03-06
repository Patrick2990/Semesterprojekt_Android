package com.graphics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// for debugging
public class FPSMeasuring extends Thread {
    private FPSDrawer fpsDrawer;
    static int counter = 0;
    public int latestFPS = 0;
    private boolean isRunning = true;
    private SharedPreferences preferences;

    public FPSMeasuring(Context context){
        fpsDrawer = new FPSDrawer(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // one time every second
    public void run() {
        while (isRunning && preferences.getBoolean("fps", false)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Log.d("FPS counter", "FPS--> " + counter);
            latestFPS = counter;
            fpsDrawer.update(counter);
            counter = 0;
        }

        fpsDrawer.fpsFactory.delete();
    }

    public void startFPS(){
        isRunning = true;
    }

    public void stopFPS(){
        isRunning = false;
    }

}