import java.io.*;
import java.net.*;

public class Client extends Thread
{
    private Socket clientSocket;
    private String clientName;
    private Room currentRoom;

    public Client(Socket socket) { clientSocket = socket; }

    public void run()
    {
        try
        {
            inputStream();
        }
        catch (IOException e)
        {
            try
            {
                clientSocket.close();
                Lobby.ClientDisconnected(Client.this);
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    private void inputStream() throws IOException
    {
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

        while (true)
        {
            String utf = dataInputStream.readUTF();

            if(clientName == null)
            {
                setClientName(utf);

                System.out.println("Connected: " + clientSocket.getRemoteSocketAddress() + " : " + getClientName());
            }

            if (utf.equals("GetRooms")) //Change
            {
                Lobby.SendRoomsToClient(this);
            }

            if (utf.equals("CreateRoom")) //Change
            {
                String roomName = dataInputStream.readUTF();
                int roomCapacity = dataInputStream.readInt();

                Lobby.CreateRoom(this, roomName, roomCapacity);
            }

            if(utf.equals("ConnectToRoom")) //Change
            {
                String roomName = dataInputStream.readUTF();

                Lobby.ClientConnectToRoom(this, roomName);
            }

            if(utf.equals("DisconnectFromRoom")) //Change
            {
                String roomName = dataInputStream.readUTF();

                Lobby.ClientDisconnectFromRoom(this, roomName);
            }

            if(utf.equals("Message")) //Change
            {
                String msg = dataInputStream.readUTF();

                currentRoom.MessageAll(clientName + ": " + msg);
            }
        }
    }

    public Socket getClientSocket()
    {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public Room getCurrentRoom()
    {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom)
    {
        this.currentRoom = currentRoom;
    }
}
