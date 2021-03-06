package com.network.Firebase;

import android.util.Log;

import com.fragments.HighScoreFragment;
import com.gamelogic.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NetworkHandler {

    private DatabaseReference mFirebaseDatabaseReference;
    private ArrayList<RemotePlayer> playerListeners;
    private String game;
    private String gamePrefix;
    private boolean multiPlayerGame;
    private String mother;
    private int minimum = 100000000;
    private int maximum = 999999999;
    private String myPlayerID;

    private enum gameStatus {
        FILLING_GAME,
        BEGUN
    }

    public NetworkHandler(boolean multiPlayerGame) {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        playerListeners = new ArrayList<>();

        this.multiPlayerGame = multiPlayerGame;

        Log.e("NetworkHandler", "NetworkHandler started. Multiplayer: " + this.multiPlayerGame);

        if (this.multiPlayerGame) {

            gamePrefix = "Game_";
            myPlayerID = "Player_";

            this.game = gamePrefix;

            // testing - adding games
            mother = "Games";
            String testGame1 = "TestGame_" + 9999999;
//            String testGame2 = "TestGame" + randomNumber(minimum, maximum);
//            String testGame3 = "TestGame" + randomNumber(minimum, maximum);

            // Remove old data
//            mFirebaseDatabaseReference.child(mother).removeValue();
//
            mFirebaseDatabaseReference.child(mother).child(testGame1).child("Status").setValue(gameStatus.BEGUN.ordinal());
//            mFirebaseDatabaseReference.child(mother).child(testGame2).child("Status").setValue(gameStatus.BEGUN.ordinal());
//            mFirebaseDatabaseReference.child(mother).child(testGame3).child("Status").setValue(gameStatus.BEGUN.ordinal());

            Log.d("NetworkHandler", "NetworkHandler started");

            searchForGame();
        }
    }


    private void searchForGame() {
        mFirebaseDatabaseReference.child(mother).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object receivedObject = dataSnapshot.getValue();
                if (receivedObject != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(receivedObject.toString());
                        Log.e("NetworkHandler", "Received json: " + jsonObject.toString());

                        Iterator<?> keys = jsonObject.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (jsonObject.get(key) instanceof JSONObject) {
                                Log.d("NetworkHandler", "Key: " + key + " : game status: " + jsonObject.get(key).toString());
                                Integer gameStatus = ((JSONObject) jsonObject.get(key)).getInt("Status");
                                if (gameStatus == NetworkHandler.gameStatus.FILLING_GAME.ordinal()) {
                                    Log.e("NetworkHandler", "Found game to join: " + key);
                                    joinGame(key);
                                    return; // stop searching for game to join in iterator
                                }
                            }
                        }

                        // creating new game if all games are filled
                        createNewGame();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NetworkHandler", "Cancel subscriebtion!");
            }
        });
    }

    public void addPlayerListener(RemotePlayer playerListener) {
        playerListeners.add(playerListener);
    }

    private void startListenGame() {
        String remotePlayerID = "Player_"; // Which player to receive data from
        if (DataContainer.instance.player.getPlayerID() == 1) {
            remotePlayerID += "2";
        } else if (DataContainer.instance.player.getPlayerID() == 2) {
            remotePlayerID += "1";
        }
        mFirebaseDatabaseReference.child(mother).child(game).child(remotePlayerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object receivedObject = dataSnapshot.getValue();
                if (receivedObject != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(receivedObject.toString());
//                        Log.d("NetworkHandler", "Received json: " + jsonObject.toString());
                        Long receivedTime = jsonObject.getLong("Time");
                        Long xPos = jsonObject.getLong("X");
                        Long yPos = jsonObject.getLong("Y");
                        int angle = jsonObject.getInt("Angle");
                        long time = System.currentTimeMillis() - receivedTime;

                        for (RemotePlayer playerListener : playerListeners) {
                            playerListener.updatePlayerPosition(xPos, yPos, angle);
                        }
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

    public void updatePlayerPosition(float centerX, float centerY, int angle) {
//        Log.d("NetworkHandler", "Updating player position on firebase: " + centerX + ", " + centerY + ". Angle: " + angle);
        Long time = System.currentTimeMillis();
        mFirebaseDatabaseReference.child(mother).child(game).child(myPlayerID).child("Time").setValue(time);
        mFirebaseDatabaseReference.child(mother).child(game).child(myPlayerID).child("X").setValue(centerX);
        mFirebaseDatabaseReference.child(mother).child(game).child(myPlayerID).child("Y").setValue(centerY);
        mFirebaseDatabaseReference.child(mother).child(game).child(myPlayerID).child("Angle").setValue(angle);

    }

    private void beginGame() {
        updatePlayerPosition(DataContainer.instance.player.getPos().x, DataContainer.instance.player.getPos().y, 0);
    }

    private void createNewGame() {
        DataContainer.instance.player.setPlayerID(1);
        this.game += String.valueOf(randomNumber(minimum, maximum)); // game subfix
        Log.e("NetworkHandler", "Creating new multiplayer game: " + game);
        myPlayerID += String.valueOf(DataContainer.instance.player.getPlayerID());
        mFirebaseDatabaseReference.child(mother).child(this.game).child("Status").setValue(gameStatus.FILLING_GAME.ordinal());
        beginGame();
        startListenGame();
    }

    private void joinGame(String game) {
        Log.e("NetworkHandler", "Joining multiplayer game: " + game);
        this.game = game;
        DataContainer.instance.player.setPlayerID(2);
        myPlayerID += String.valueOf(DataContainer.instance.player.getPlayerID());
        mFirebaseDatabaseReference.child(mother).child(this.game).child("Status").setValue(NetworkHandler.gameStatus.BEGUN.ordinal());
        beginGame();
        startListenGame();
    }

    private int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
