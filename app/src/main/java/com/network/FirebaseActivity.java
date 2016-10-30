package com.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patrickkaalund.semesterprojekt_android.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private Button mSendButton;
    private EditText mMessageEditText;
    private TextView mMessageReceivedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mSendButton = (Button) findViewById(R.id.button_send_message);
        mMessageEditText = (EditText) findViewById(R.id.text_message);
        mMessageReceivedText = (TextView) findViewById(R.id.text_console);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String friendlyMessage = mMessageEditText.getText().toString();
                //mFirebaseDatabaseReference.child("Player").push().setValue(friendlyMessage);
                //mMessageEditText.setText("");
                sendMessage("");
            }
        });

        mFirebaseDatabaseReference.child("Player").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object received = dataSnapshot.getValue();
                if(received != null){
                    long time = System.nanoTime() - Long.parseLong(received.toString().split("=")[1].substring(0,13));
                    mMessageReceivedText.setText(String.valueOf(time/1000000) + " millis");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseActivity", "Cancel subscribtion!");
            }
        });
    }

    public void sendMessage(String message){
        mFirebaseDatabaseReference.child("Player").child("Time").setValue(System.nanoTime());
    }
}


// ZERO MQ

//public class FirebaseActivity extends Activity {
//    private TextView textView;
//    private EditText editText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_network);
//
//        textView = (TextView)findViewById(R.id.text_console);
//        editText = (EditText)findViewById(R.id.text_message);
//
//        new Thread(new ZeroMQServer(serverMessageHandler)).start();
//
//        findViewById(R.id.button_send_message).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new ZeroMQMessageTask(clientMessageHandler).execute(getTaskInput());
//                    }
//
//                    protected String getTaskInput() {
//                        return editText.getText().toString();
//                    }
//                });
//    }
//
//    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
//
//    private static String getTimeString() {
//        return DATE_FORMAT.format(new Date());
//    }
//
//    private void serverMessageReceived(String messageBody) {
//        // clear text
//        textView.setText("");
//        textView.append(getTimeString() + " - server received: " + messageBody + "\n");
//    }
//
//    private void clientMessageReceived(String messageBody) {
//        textView.append(getTimeString() + " - client received: " + messageBody + "\n");
//    }
//
//    private final MessageListenerHandler serverMessageHandler = new MessageListenerHandler(
//            new IMessageListener() {
//                @Override
//                public void messageReceived(String messageBody) {
//                    serverMessageReceived(messageBody);
//                }
//            },
//            Util.MESSAGE_PAYLOAD_KEY);
//
//    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(
//            new IMessageListener() {
//                @Override
//                public void messageReceived(String messageBody) {
//                    clientMessageReceived(messageBody);
//                }
//            },
//            Util.MESSAGE_PAYLOAD_KEY);
//}
