package com.fragments;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.audio.AudioPlayer;
import com.example.patrickkaalund.semesterprojekt_android.R;
import com.graphics.Entity;
import com.graphics.SpriteEntityFactory;
import com.network.Firebase.NetworkHandler;

import java.util.ArrayList;


public class HighScoreFragment extends Fragment implements View.OnClickListener {

    View view;
    private AudioPlayer audioPlayer;
    private NetworkHandler networkHandler;

    private Entity highScoreDrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_high_score, container, false);
        this.view = view;

//        DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();
//        SpriteEntityFactory highScoreFactory = new SpriteEntityFactory(R.drawable.numbers_fps, 120, 120, 11, 1, new PointF(0, 0));
//        highScoreDrawer = highScoreFactory.createEntity();
//
//        highScoreDrawer.placeAt(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        highScoreDrawer.setCurrentSprite(0);

        networkHandler = new NetworkHandler();
        networkHandler.requestHighScoreList(this);

        TextView mainMenuButton = (TextView) view.findViewById(R.id.buttonMainMenu);

        mainMenuButton.setOnClickListener(this);

        audioPlayer = new AudioPlayer(view.getContext());

        view.bringToFront();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMainMenu:
                Log.d("Main", "Clicked");

                v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.view_clicked));

                audioPlayer.playAudioFromRaw(R.raw.click);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .remove(this)
                        .add(R.id.activity_main_menu, new ButtonMainFragment())
                        .commit();
                break;
        }
    }

    public void fillHighScore(ArrayList<String> info){
        Log.e("HighScoreFragment", "Info: " + info.toString());

        // Draw top 3 on high score list
        for(int i = 0; i < 3; i++){
            Log.e("HighScoreFragment", "Draw highscore: " + i + " with value: " + info.get(i));
        }
    }
}
