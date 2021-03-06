package com.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activities.InGame;
import com.activities.Options;
import com.audio.AudioPlayer;
import com.example.patrickkaalund.semesterprojekt_android.R;
import com.gamelogic.DataContainer;


public class ButtonMainFragment extends Fragment implements View.OnClickListener {

    View view;

    private AudioPlayer audioPlayer;

    private RelativeLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        this.view = view;

        audioPlayer = new AudioPlayer(getContext());

        TextView nextButton = (TextView) view.findViewById(R.id.buttonPlay);
        TextView play = (TextView) getActivity().findViewById(R.id.buttonPlay);
        TextView playMulti = (TextView) getActivity().findViewById(R.id.buttonPlayMulti);
        TextView settings = (TextView) getActivity().findViewById(R.id.buttonSettings);
        TextView quit = (TextView) getActivity().findViewById(R.id.buttonQuit);
        TextView highscore = (TextView) getActivity().findViewById(R.id.buttonHighscore);

        nextButton.setOnClickListener(this);
        play.setOnClickListener(this);
        playMulti.setOnClickListener(this);
        settings.setOnClickListener(this);
        quit.setOnClickListener(this);
        highscore.setOnClickListener(this);

        view.bringToFront();

        // Do start-up animations
        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.RLayout);
        layout = (RelativeLayout) getActivity().findViewById(R.id.OuterRelativeLayout);
        relativeLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.startup));
        layout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.startup));
        relativeLayout.setVisibility(View.VISIBLE);

        audioPlayer.playAudioFromRaw(R.raw.baretta);

        return view;
    }


    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.buttonPlay:
                    play(v);
                    DataContainer.instance.multiplayerGame = false;
                    break;

                case R.id.buttonPlayMulti:
                    Log.d("ButtonMainFragment", "Multiplayer game!");

                    v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.view_clicked));
                    audioPlayer.playAudioFromRaw(R.raw.click);

                    layout.setVisibility(View.INVISIBLE);

                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .remove(this)
                            .add(R.id.start_fragment_holder, new MultiplayerFragment())
                            .addToBackStack(null)
                            .commit();
                    break;

                case R.id.buttonSettings:
                    v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_clicked));
                    audioPlayer.playAudioFromRaw(R.raw.click);
                    Intent settings = new Intent(getContext(), Options.class);
                    startActivity(settings);
                    break;

                case R.id.buttonQuit:
                    v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_clicked));
                    audioPlayer.playAudioFromRaw(R.raw.click);

                    // Hackish way to mimic back-button press
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    break;

                case R.id.buttonHighscore:
                    v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_clicked));
                    audioPlayer.playAudioFromRaw(R.raw.click);

                    layout.setVisibility(View.INVISIBLE);

                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .remove(this)
                            .add(R.id.start_fragment_holder, new HighScoreFragment())
                            .addToBackStack(null)
                            .commit();

                    break;
            }
    }

    public void play(View v){
        // Add dark overlay to screen
        getActivity().findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.overlay).bringToFront();

        // Bring progressBar to top
        getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.progressBar).bringToFront();
        getActivity().findViewById(R.id.progressBar).invalidate();

        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.view_clicked));

        audioPlayer.playAudioFromRaw(R.raw.click);
        Intent play = new Intent(getContext(), InGame.class);
        startActivity(play);
    }

    public void onResume() {
        // Remove dark overlay from screen
        getActivity().findViewById(R.id.overlay).setVisibility(View.INVISIBLE);

        getActivity().findViewById(R.id.OuterRelativeLayout).setVisibility(View.VISIBLE);

        // Hide progressBar
        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

        // Set music track
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putInt("track", R.raw.menu_music).apply();

        super.onResume();
    }
}
