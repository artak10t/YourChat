package com.flavorlessart.yourgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class Room extends AppCompatActivity
{
    public static Room singleton;

    private Button chatSend;
    private EditText chatText;
    private TextView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        singleton = this;

        chatSend = findViewById(R.id.chatSendButton);
        chatText = findViewById(R.id.editTextChat);
        chatView = findViewById(R.id.chatView);
        chatView.setMovementMethod(new ScrollingMovementMethod());

        chatSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });
    }


    private void sendMessage()
    {
        Client.singleton.SendMessage(chatText.getText().toString());
    }

    public void displayMessage(String msg)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                chatView.append(msg + "\n");
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Client.singleton.DisconnectFromRoom();
        finishAffinity();

        Intent intent = new Intent(getApplicationContext(), Lobby.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Client.singleton.GetRoomsFromServer();
    }
}