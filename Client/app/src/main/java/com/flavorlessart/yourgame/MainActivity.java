package com.flavorlessart.yourgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{
    public static MainActivity singleton;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton = this;

        Init();
    }

    private void Init()
    {
        connectButton = findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                OnConnectClick(v);
            }
        });
    }

    private void OnConnectClick(View v)
    {
        EditText nameText = findViewById(R.id.editTextName);
        String name = nameText.getText().toString();

        Client client = new Client(name);
        Client.singleton = client;
        Client.singleton.setView(v);
        Client.singleton.start();
    }

    public void Connected()
    {
        Intent lobby = new Intent(this, Lobby.class);
        finish();
        startActivity(lobby);
    }
}