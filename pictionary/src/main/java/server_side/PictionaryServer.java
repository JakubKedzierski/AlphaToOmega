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
	private int playersToStartGame = 2;
	private @Getter boolean disconnected = false;
	private @Getter ConcurrentLinkedQueue<ClientHandler> users = new ConcurrentLinkedQueue<ClientHandler>();
	private @Getter int validUsers = 0;
	private Pictionary game = null;
	private ServerSocket serverSocket = null;
	private int acceptedConnections=0;
	private @Getter boolean testMode=false;

	public PictionaryServer(int playersToStartGame,boolean testMode) {
		this.playersToStartGame = playersToStartGame;
		this.testMode=testMode;
		new Thread(this).start();
	}

	public PictionaryServer() {
		new Thread(this).start();
	}

	public static void main(String[] args) {
		new PictionaryServer();

	}

	private void listetningLoop() throws IOException {
		while (acceptedConnections < playersToStartGame) {
			Socket clientSocket = serverSocket.accept();
			acceptedConnections++;
			if (clientSocket != null) {
				System.out.println("Connection accepted");
				ClientHandler userHandler = new ClientHandler(this, clientSocket);
				users.add(userHandler);
			}
		}
	}

	@Override
	public void run() {
		try  {
			this.serverSocket = new ServerSocket(SERVER_PORT);
			int port = serverSocket.getLocalPort();
			String address = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Server starts on port  " + port);
			System.out.println("Host address: " + address);
			
			if(!testMode)
			game = new Pictionary(this, playersToStartGame);

			listetningLoop();

		} catch (IOException ioException) {
			
			if (disconnected) return;
			else disconnectServer();

			throw new IllegalArgumentException("Probably another server is listening on this port.");
		}

	}

	public void addUserToGame(String name) {
		game.addUser(name);
		validUsers++;
		if (validUsers == playersToStartGame) {
			startGame();
		}
	}

	public void startGame() {
		try {
			Thread.sleep(3000);
			String jsonMessage = PictionaryProtocolParser.createProtocolMessage("server", "broadcast", "gameInfo",
					"StartGame");
			sendBroadcastMessage("server", jsonMessage);
			game.startGame();
		} catch (IOException | PictionaryException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void disconnectServer() {
		disconnected = true;
		try {

			for (ClientHandler handler : users) {
				handler.diconnectClient();
			}
			
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientHandler getClientHandlerById(final String id) {
		for (ClientHandler handler : users) {
			if (handler.getUsername() == id) {
				return handler;
			}
		}
		return null;
	}

	public boolean isNameTaken(String userName) {
		for (ClientHandler handler : users) {
			if (userName.equals(handler.getUsername())) {
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
		if(game.isGameRunning()) game.cleanUpAndUnexpectedEndGame();
		users.remove(clientHandler);
		validUsers--;
		
		if(validUsers==0) System.exit(0);
	}

	public ArrayList<String> getUsersIdList() {
		ArrayList<String> nameList = new ArrayList<String>();
		for (ClientHandler handler : users) {
			String name = handler.getUsername();
			nameList.add(name);
		}
		return nameList;
	}

	public void sendGameInfo(String userId, String gameInfo) throws PictionaryException, IOException {
		ClientHandler handler = getClientHandlerById(userId);
		handler.sendMessageFromServerToClient("gameInfo", gameInfo);
	}

	public void sendMessageToClient(String username, String message) throws IOException {
		ClientHandler handler = getClientHandlerById(username);
		handler.sendMessageDirectlyToHandledClient(message);
	}

	public void sendBroadcastMessage(String senderUsername, String message) throws IOException {
		for (ClientHandler handler : users) {
			if (!handler.getUsername().equals(senderUsername)) {
				handler.sendMessageDirectlyToHandledClient(message);
			}
		}
	}

	@Override
	public void checkWord(String word, String username) {
		game.checkWord(word, username);
	}

}

@EqualsAndHashCode
class ClientHandler implements Runnable {

	private @Getter @Setter String username = null;
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

		username = userDeclaredName;
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
					return;
				} catch (ClassNotFoundException e) {
					throw new PictionaryException("Name validation exception");
				}

			} else {
				username = userDeclaredName;
				sendMessageFromServerToClient("NameValidation", "OK");
				if(!server.isTestMode())  server.addUserToGame(userDeclaredName);
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
		String message = messageInfo.get(PictionaryProtocolPool.MESSAGE);

		if (sender.equals(username)) {
			if (receiver.equals("broadcast")) {
				server.sendBroadcastMessage(sender, plainMessage);

			} else if (receiver.equals("server")) {
				if (messageInfo.get(PictionaryProtocolPool.MESSAGETYPE).equals("Error")) {

					if (message.equals("disconected")) {
						diconnectClient();
					}

				} else if (messageInfo.get(PictionaryProtocolPool.MESSAGETYPE).equals("guessedWord")) {
					server.checkWord(message, username);
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
		return "Handler of " + username + "\n";

	}

}
