import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Room
{
	private List<Client> clients = new LinkedList<>();

	private String roomName;
	private int roomCapacity;

	public Room(String roomName, int roomCapacity)
	{
		this.roomName = roomName;
		this.roomCapacity = roomCapacity;
	}

	public void ClientConnected(Client client) throws IOException
	{
		MessageAll(client.getClientName() + " Connected!");

		clients.add(client);
		client.setCurrentRoom(this);
		System.out.println(client.getClientName() + " - Connected To Room - " + this.roomName);
	}

	public void ClientDisconnected(Client client) throws IOException
	{
		clients.remove(client);
		client.setCurrentRoom(null);
		System.out.println(client.getClientName() + " - Disconnected From Room - " + this.roomName);
		MessageAll(client.getClientName() + " Disconnected!");
	}

	public void MessageAll(String msg) throws IOException
	{
		for (Client client : clients)
		{
			DataOutputStream dataOutputStream = new DataOutputStream(client.getClientSocket().getOutputStream());

			dataOutputStream.writeUTF("DisplayMessage");
			dataOutputStream.flush();
			dataOutputStream.writeUTF(msg);
			dataOutputStream.flush();
		}
	}

	public String getRoomName()
	{
		return roomName;
	}

	public int getRoomCapacity()
	{
		return roomCapacity;
	}

	public void setRoomCapacity(int newRoomCapacity)
	{
		this.roomCapacity = newRoomCapacity;
	}

	public int getClientsInRoom()
	{
		return clients.size();
	}

	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}
}
