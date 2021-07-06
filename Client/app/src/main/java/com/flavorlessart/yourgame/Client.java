package com.flavorlessart.yourgame;

import android.content.Intent;
import android.os.Debug;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.*;
import java.net.*;

public class Client extends Thread
{
    public static Client singleton;

    private static final String IP = "192.168.0.3";
    private static final int PORT = 25565;

    public Socket socket;
    private Thread inputThread;
    private View view;

    private String name;
    private String roomName;

    public Client (String name)
    {
        this.name = name;
    }

    public void run()
    {
        try
        {
            OnConnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void GetRoomsFromServer()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("GetRooms"); //Change
                    outputStream.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void CreateRoom (String RoomName, int maxUsers)
    {
        setRoomName(RoomName);

        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("CreateRoom"); //Change
                    outputStream.flush();
                    outputStream.writeUTF(roomName);
                    outputStream.flush();
                    outputStream.writeInt(maxUsers);
                    outputStream.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void ConnectToRoom()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("ConnectToRoom"); //Change
                    outputStream.flush();
                    outputStream.writeUTF(roomName);
                    outputStream.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void SendMessage(String msg)
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("Message"); //Change
                    outputStream.flush();
                    outputStream.writeUTF(msg);
                    outputStream.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void DisconnectFromRoom()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("DisconnectFromRoom"); //Change
                    outputStream.flush();
                    outputStream.writeUTF(roomName);
                    outputStream.flush();

                    setRoomName(null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void OnConnect() throws IOException
    {
        try
        {
            socket = new Socket(IP, PORT);

            if(socket.isConnected())
            {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(name);
                outputStream.flush();

                inputThread = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            inputStream();
                        }
                        catch (IOException g)
                        {
                            Snackbar.make(view, "Cannot create room", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                };

                inputThread.start();

                MainActivity.singleton.Connected();
            }
        }
        catch (ConnectException e)
        {
            Snackbar.make(view, "Cannot connect to servers", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void inputStream() throws IOException
    {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        while (true)
        {
            String utf = inputStream.readUTF();

            if(utf.equals("RoomNameInUse")) //Change
                Snackbar.make(view, "Room name in use", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            if(utf.equals("Rooms"))
            {
                try
                {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    String[][] rooms = (String[][]) objectInputStream.readObject();

                    Lobby.singleton.CreateRoomButton(rooms);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            if(utf.equals("RoomCreated")) //Change
            {
                ConnectToRoom();
            }

            if(utf.equals("ConnectedToRoom"))
            {
                String roomName = inputStream.readUTF();
                setRoomName(roomName);

                if(CreateRoom.singleton != null)
                    CreateRoom.singleton.finish();

                Lobby.singleton.ConnectToRoom();
            }

            if(utf.equals("DisplayMessage"))
            {
                String msg = inputStream.readUTF();

                Room.singleton.displayMessage(msg);
            }
        }
    }

    public void setView(View view)
    {
        this.view = view;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getRoomName()
    {
        return roomName;
    }
}
