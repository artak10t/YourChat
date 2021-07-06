package com.flavorlessart.yourgame;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.DataOutputStream;
import java.io.IOException;

public class Lobby extends AppCompatActivity
{
    public static Lobby singleton;

    private LinearLayout lobbyLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        singleton = this;
        lobbyLinearLayout = (LinearLayout) findViewById(R.id.lobbyLinearLayout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createRoomFab);

        Client.singleton.GetRoomsFromServer();
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createRoomClick();
            }
        });

        Thread thread = new Thread()
        {
            public void run()
            {
                while (true && Client.singleton.getRoomName() == null && !Client.singleton.socket.isClosed())
                {
                    try
                    {
                        sleep(1000);

                        Client.singleton.GetRoomsFromServer();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
    }

    public void CreateRoomButton(String[][] rooms)
    {
        for(int i = 0; i < rooms.length; i++)
        {
            Button button = new Button(this);
            button.setText(rooms[i][0] + " : " + rooms[i][1]);

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Client.singleton.setRoomName(rooms[finalI][0]);
                    Client.singleton.ConnectToRoom();
                }
            });

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    lobbyLinearLayout.removeAllViews();

                    lobbyLinearLayout.addView(button);
                }
            });
        }

        if(rooms.length == 0)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    lobbyLinearLayout.removeAllViews();
                }
            });
        }
    }

    public void ConnectToRoom()
    {
        Intent room = new Intent(this, Room.class);
        startActivity(room);
    }

    private void createRoomClick()
    {
        Intent createRoom = new Intent(this, CreateRoom.class);
        startActivity(createRoom);
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            Client.singleton.socket.close();

            finishAffinity();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}