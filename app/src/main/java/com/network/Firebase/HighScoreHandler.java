package com.network.Firebase;

import android.util.Log;

import com.fragments.HighScoreFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class HighScoreHandler {

    private DatabaseReference mFirebaseDatabaseReference;
    private String highScoreLocation;

    public HighScoreHandler() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.highScoreLocation = "HighScore";
    }

    public void requestHighScoreList(final HighScoreFragment highScoreFragment) {
        // get high score list from server
        mFirebaseDatabaseReference.child(highScoreLocation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object receivedObject = dataSnapshot.getValue();

                Comparator<HighScoreObject> comparator = new HighScoreComparator();
                PriorityQueue<HighScoreObject> queue = new PriorityQueue<>(10, comparator);

                if (receivedObject != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(receivedObject.toString());
                        Iterator<?> keys = jsonObject.keys();

                        Log.e("NetworkHandler", "Received: " + jsonObject.toString());

                        // Add highscore objects to queue
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
//                            Log.d("NetworkHandler", "Key: " + key + ". Value is: " + jsonObject.get(key));
                            queue.offer(new HighScoreObject(key, (int) jsonObject.get(key)));
                        }

                        highScoreFragment.fillHighScore(queue);
                        return;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                queue.add(new HighScoreObject("N/A", 0));
                highScoreFragment.fillHighScore(queue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NetworkHandler", "Cancel subscription!");
            }
        });
    }

    // publish HighScore
    public void publishHighScore(final String userName, final int points){

        // get high score list from server
        mFirebaseDatabaseReference.child(highScoreLocation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object receivedObject = dataSnapshot.getValue();

                Comparator<HighScoreObject> comparator = new HighScoreComparator();
                PriorityQueue<HighScoreObject> queue = new PriorityQueue<>(10, comparator);

                if (receivedObject != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(receivedObject.toString());
                        Iterator<?> keys = jsonObject.keys();

                        Log.e("NetworkHandler", "Received: " + jsonObject.toString());

                        // Add highscore objects to queue
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
//                            Log.d("NetworkHandler", "Key: " + key + ". Value is: " + jsonObject.get(key));
                            queue.offer(new HighScoreObject(key, (int) jsonObject.get(key)));
                        }

                        HighScoreObject lowestHighScore = null;
                        while(queue.peek() != null){
                            lowestHighScore = queue.remove();
                        }

                        if(lowestHighScore != null && points > lowestHighScore.value){
                            Log.e("NetworkHandler", "Updating lowest highscore element: " + userName + " with value: " + points);
                            mFirebaseDatabaseReference.child(highScoreLocation).child(userName).setValue(points);
                            Log.e("NetworkHandler", "Removing lowest highscore element: " + lowestHighScore.name + " with value: " + lowestHighScore.value);
                            // Delete old highscore if not yourself
                            if(!(lowestHighScore.name.equals(userName))){
                                mFirebaseDatabaseReference.child(highScoreLocation).child(lowestHighScore.name).removeValue();
                            }

                        }else{
                            Log.e("NetworkHandler", "Not updating!!!: " + lowestHighScore.name + " with value: " + lowestHighScore.value);
                        }

                        return;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NetworkHandler", "Cancel subscription!");
            }
        });
    }


    // Sorting highest to lowest
    public class HighScoreComparator implements Comparator<HighScoreObject> {
        @Override
        public int compare(HighScoreObject lhs, HighScoreObject rhs) {
            return rhs.value - lhs.value;
        }
    }
}
