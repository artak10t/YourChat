import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Lobby
{
	private static List<Client> clients = new LinkedList<>();
	private static List<Room> rooms = new LinkedList<>();

	public static void ClientConnected(Client client)
	{
		clients.add(client);
	}

	public static void ClientDisconnected(Client client) throws IOException
	{
		try
		{
			ClientDisconnectFromRoom(client, client.getCurrentRoom().getRoomName());
			System.out.println("Disconnected: " + client.getClientSocket().getRemoteSocketAddress() + " : " + client.getClientName());
			clients.remove(client);
		}
		catch (Exception e)
		{
			System.out.println("Disconnected: " + client.getClientSocket().getRemoteSocketAddress() + " : " + client.getClientName());
			clients.remove(client);
		}
	}

	public static void ClientConnectToRoom(Client client, String roomName) throws IOException
	{
		for(Room room : rooms)
		{
			if(room.getRoomName().equals(roomName))
			{
				if (room.getClientsInRoom() != room.getRoomCapacity())
				{
					room.ClientConnected(client);
					clients.remove(client);

					DataOutputStream outputStream = new DataOutputStream(client.getClientSocket().getOutputStream());
					outputStream.writeUTF("ConnectedToRoom"); // Change
					outputStream.flush();
					outputStream.writeUTF(roomName); // Change
					outputStream.flush();

					break;
				}
			}
		}
	}

	public static void ClientDisconnectFromRoom(Client client, String roomName) throws IOException
	{
		for(Room room : rooms)
		{
			if(room.getRoomName().equals(roomName))
			{
				room.ClientDisconnected(client);
				clients.add(client);

				if(room.getClientsInRoom() == 0)
					Lobby.DeleteRoom(room);

				break;
			}
		}
	}

	public static void SendRoomsToClient(Client client) throws IOException
	{
		DataOutputStream outputStream = new DataOutputStream(client.getClientSocket().getOutputStream());

		outputStream.writeUTF("Rooms");
		outputStream.flush();

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(getAllRooms());
		objectOutputStream.flush();
	}

	public static void CreateRoom(Client host, String roomName, int roomCapacity)
	{
		if (roomCapacity <= 1)
			return;

		Room newRoom = new Room(roomName, roomCapacity);
		boolean nameTaken = false;

		for(Room room : rooms)
		{
			if(room.getRoomName().equals(newRoom.getRoomName()))
			{
				nameTaken = true;

				break;
			}
		}

		if(nameTaken)
		{
			try
			{
				DataOutputStream outputStream = new DataOutputStream(host.getClientSocket().getOutputStream());
				outputStream.writeUTF("RoomNameInUse"); // Change
				outputStream.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				rooms.add(newRoom);

				DataOutputStream outputStream = new DataOutputStream(host.getClientSocket().getOutputStream());
				outputStream.writeUTF("RoomCreated"); // Change
				outputStream.flush();

				System.out.println("Room Created: " + roomName + " Max Users: " + roomCapacity);
			}
			catch (Exception e)
			{
				DeleteRoom(newRoom);
			}
		}
	}

	public static void DeleteRoom(Room room)
	{
		System.out.println("Room Deleted: " + room.getRoomName());

		rooms.remove(room);
	}

	public static String[][] getAllRooms()
	{
		String[][] returnRooms = new String[rooms.size()][2];
		int i = 0;
		for (Room room : rooms)
		{
			returnRooms[i][0] = room.getRoomName();

			String users = String.valueOf(room.getClientsInRoom()) + "/" + String.valueOf(room.getRoomCapacity());
			returnRooms[i][1] = users;
		}
		return returnRooms;
	}
}