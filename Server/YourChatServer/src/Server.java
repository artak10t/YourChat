import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 25565;

    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is online!");

        while (true)
        {
            Socket socket = serverSocket.accept();
            Client client = new Client(socket);
            client.start();

            Lobby.ClientConnected(client);
        }
    }
}
