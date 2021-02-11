package server_side;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.pictionary.Pictionary;

public class PictionaryServer implements Runnable, GameCommunication, ServerHandlerInterface {
	public static int SERVER_PORT = 25000;
	public static int PLAYERS_TO_START_GAME = 2;
	private @Getter boolean serverRunning = false;
	private @Getter ConcurrentLinkedQueue<ClientHandler> users = new ConcurrentLinkedQueue<ClientHandler>();
	private Pictionary game = null;

	public PictionaryServer() {
		new Thread(this).start();
	}

	public static void main(String[] args) {
		new PictionaryServer();

	}

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
			serverRunning = true;
			int port = serverSocket.getLocalPort();
			String address = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Server starts on port  " + port);
			System.out.println("Host address: " + address);
			
			game = new Pictionary(this);

			while (users.size() < PLAYERS_TO_START_GAME) {
				Socket clientSocket = serverSocket.accept();
				if (clientSocket != null) {
					System.out.println("Connection accepted");
					ClientHandler userHandler = new ClientHandler(this, clientSocket);
					users.add(userHandler);
				}
			}

			startGame();
			while (serverRunning) { // to keep thread alive and server socket open
			}

		} catch (IOException ioException) {
			System.out.println("Issues with listening thread");
			return;
		}

	}

	public void addUserToGame(String name) {
		game.addUser(name);
	}

	public void startGame() {
		try {
			Thread.sleep(5000);
			String jsonMessage = PictionaryProtocolParser.createProtocolMessage("server", "broadcast", "gameInfo","StartGame");
			sendBroadcastMessage("server", jsonMessage);
		} catch (IOException | PictionaryException | InterruptedException  e) {
			System.out.println("rzucam");
			e.printStackTrace();
		}

	}

	public void disconnectServer() {
		serverRunning = false;
		// inform clients about disconnection if there are clients + end up game
	}

	public ClientHandler getClientHandlerById(final String id) {
		for (ClientHandler handler : users) {
			if (handler.getUserId() == id) {
				return handler;
			}
		}
		return null;
	}

	public boolean isNameTaken(String userName) {
		for (ClientHandler handler : users) {
			if (userName.equals(handler.getUserId())) {
				return true;
			}
		}

		return false;
	}

	public int users() {
		return users.size();
	}

	public void addClientHandler(ClientHandler clientHandler) {
		users.add(clientHandler);
	}

	public void removeHandler(ClientHandler clientHandler) {
		users.remove(clientHandler);
	}

	public ArrayList<String> getUsersIdList() {
		ArrayList<String> nameList = new ArrayList<String>();
		for (ClientHandler handler : users) {
			String name = handler.getUserId();
			nameList.add(name);
		}
		return nameList;
	}

	@Override
	public void sendHostInfo(String userId, String word) throws IOException, PictionaryException {
		ClientHandler handler = getClientHandlerById(userId);
		handler.sendMessageFromServerToClient("gameInfo", "{\"typeOfPlayer\":\"host\"}");
		handler.sendMessageFromServerToClient("gameInfo", "{\"word\":\"" + word + "\"}");
	}

	@Override
	public void sendListenerInfo(String userId) throws PictionaryException, IOException {
		ClientHandler handler = getClientHandlerById(userId);
		handler.sendMessageFromServerToClient("gameInfo", "{\"typeOfPlayer\":\"listener\"}");
	}

	@Override
	public void sendEndGameInfo() throws PictionaryException, IOException {
		for (ClientHandler handler : users) {
			handler.sendMessageFromServerToClient("gameInfo", "game ended");
		}

	}

	public void sendMessageToClient(String username, String message) throws IOException {
		for (ClientHandler handler : users) {
			if (handler.getUserId().equals(username)) {
				handler.sendMessageDirectlyToHandledClient(message);
			}
		}
	}

	public void sendBroadcastMessage(String senderUsername, String message) throws IOException {
		for (ClientHandler handler : users) {
			if (!handler.getUserId().equals(senderUsername)) {
				handler.sendMessageDirectlyToHandledClient(message);
			}
		}
	}

}

@EqualsAndHashCode
class ClientHandler implements Runnable {

	private @Getter @Setter String userId = null;
	private ServerHandlerInterface server = null;
	private @Getter Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean connected = false;

	ClientHandler(PictionaryServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());) {

			connected = true;
			System.out.println("Client handler starts");
			outputStream = output;
			inputStream = input;
			String message = (String) inputStream.readObject();

			if (message != null) {
				newConnectionStartup(message);
			}

			while (true) {
				message = (String) input.readObject();
				parseProtocolMessage(message);

				if (!connected) {
					System.out.println("Client handler exits");
					return;
				}
			}

		} catch (IOException | ClassNotFoundException exception) {
			System.out.println("Client connecetion failed");
			server.removeHandler(this);
			return;
		} catch (PictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void nameValidation(String userDeclaredName) throws PictionaryException, IOException {
		while (server.isNameTaken(userDeclaredName)) {

			sendMessageFromServerToClient("NameValidation", "InvalidName");

			try {
				userDeclaredName = (String) inputStream.readObject();
			} catch (ClassNotFoundException exception) {
				System.out.println("Name validation went wrong");
			}

		}

		userId = userDeclaredName;
	}

	private void newConnectionStartup(String message) throws PictionaryException, IOException {

		HashMap<PictionaryProtocolPool, String> messageInfo = PictionaryProtocolParser.parseProtocol(message);

		if (messageInfo.get(PictionaryProtocolPool.MESSAGETYPE).equals("NameValidation")) {

			String userDeclaredName = messageInfo.get(PictionaryProtocolPool.MESSAGE);

			if (server.isNameTaken(userDeclaredName)) {
				sendMessageFromServerToClient("NameValidation", "InvalidName");

				try {
					String userNewName;
					userNewName = (String) inputStream.readObject();
					newConnectionStartup(userNewName);
				} catch (ClassNotFoundException e) {
					throw new PictionaryException("Name validation exception");
				}

			} else {
				userId = userDeclaredName;
				server.addUserToGame(userDeclaredName);
				sendMessageFromServerToClient("NameValidation", "OK");
			}
		} else {
			throw new PictionaryException("Message dosent contain name attribute.");
		}

	}

	public void sendMessageFromServerToClient(String messageType, String message)
			throws IOException, PictionaryException {
		try {
			String jsonMessage = PictionaryProtocolParser.createProtocolMessage("server", "client", messageType,
					message);
			sendMessageDirectlyToHandledClient(jsonMessage);
		} catch (JsonProcessingException exception) {
			throw new PictionaryException("Wrong message atributes");
		}
	}

	public void sendMessageDirectlyToHandledClient(String message) throws IOException {
		outputStream.writeObject(message);
	}

	public void parseProtocolMessage(String plainMessage) throws PictionaryException, IOException {
		HashMap<PictionaryProtocolPool, String> messageInfo = PictionaryProtocolParser.parseProtocol(plainMessage);
		String sender = messageInfo.get(PictionaryProtocolPool.SENDER);
		String receiver = messageInfo.get(PictionaryProtocolPool.RECEIVER);
		System.out.println(plainMessage);
		
		if (sender.equals(userId)) {
			if (receiver.equals("broadcast")) {
				server.sendBroadcastMessage(sender, plainMessage);

			} else if (receiver.equals("server")) {
				if (messageInfo.get(PictionaryProtocolPool.MESSAGETYPE).equals("Error")) {

					if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("disconected")) {
						diconnectClient();
					}
				} else if (messageInfo.get(PictionaryProtocolPool.MESSAGETYPE).equals("guessedWord")) {
					System.out.println(plainMessage);
				}

			} else {
				server.sendMessageToClient(receiver, plainMessage);
			}

		}

	}

	public void diconnectClient() throws IOException {
		if (socket != null)
			socket.close();
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
		server.removeHandler(this);
		connected = false;
	}

	@Override
	public String toString() {
		return "Handler of " + userId + "\n";

	}

}
