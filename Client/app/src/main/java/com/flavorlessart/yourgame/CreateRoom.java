package com.flavorlessart.yourgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class CreateRoom extends AppCompatActivity
{
    public static CreateRoom singleton;

    private Button createRoomButton;
    private NumberPicker maxUsersNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        singleton = this;
        createRoomButton = findViewById(R.id.createRoomButton);
        maxUsersNumber = findViewById(R.id.maxUsersNumber);
        maxUsersNumber.setMaxValue(999);
        maxUsersNumber.setMinValue(2);
        createRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRoomCreate(v);
            }
        });
    }

    private void onRoomCreate(View v)
    {
        EditText nameText = findViewById(R.id.editTextRoomName);
        String roomName = nameText.getText().toString();

        Client.singleton.setView(v);
        Client.singleton.CreateRoom(roomName, maxUsersNumber.getValue());
    }
}