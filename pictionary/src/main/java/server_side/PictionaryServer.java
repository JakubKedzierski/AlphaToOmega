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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.pictionary.Pictionary;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryServer.
 */
public class PictionaryServer implements Runnable, GameCommunication, ServerHandlerInterface {
	
	/** The server port. */
	public static int SERVER_PORT = 25000;
	
	/** The players to start game. */
	private int playersToStartGame = 2;
	
	/** The disconnected. */
	private 
 /**
  * Checks if is disconnected.
  *
  * @return true, if is disconnected
  */
 @Getter boolean disconnected = false;
	
	/** The users. */
	private 
 /**
  * Gets the users.
  *
  * @return the users
  */
 @Getter ConcurrentLinkedQueue<ClientHandler> users = new ConcurrentLinkedQueue<ClientHandler>();
	
	/** The valid users. */
	private 
 /**
  * Gets the valid users.
  *
  * @return the valid users
  */
 @Getter int validUsers = 0;
	
	/** The game. */
	private Pictionary game = null;
	
	/** The server socket. */
	private ServerSocket serverSocket = null;
	
	/** The accepted connections. */
	private int acceptedConnections=0;
	
	/** The test mode. */
	private 
 /**
  * Checks if is test mode.
  *
  * @return true, if is test mode
  */
 @Getter boolean testMode=false;

	/**
	 * Instantiates a new pictionary server.
	 *
	 * @param playersToStartGame the players to start game
	 * @param testMode the test mode
	 */
	public PictionaryServer(int playersToStartGame,boolean testMode) {
		this.playersToStartGame = playersToStartGame;
		this.testMode=testMode;
		new Thread(this).start();
	}

	/**
	 * Instantiates a new pictionary server.
	 */
	public PictionaryServer() {
		new Thread(this).start();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new PictionaryServer();

	}

	/**
	 * Listetning loop.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Run.
	 */
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

	/**
	 * Adds the user to game.
	 *
	 * @param name the name
	 */
	public void addUserToGame(String name) {
		game.addUser(name);
		validUsers++;
		if (validUsers == playersToStartGame) {
			startGame();
		}
	}

	/**
	 * Start game.
	 */
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

	/**
	 * Disconnect server.
	 */
	public void disconnectServer() {
		disconnected = true;
		try {

			for (ClientHandler handler : users) {
				handler.diconnectClient();
			}
			
			if(serverSocket!=null && !serverSocket.isClosed()) serverSocket.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the client handler by id.
	 *
	 * @param id the id
	 * @return the client handler by id
	 */
	public ClientHandler getClientHandlerById(final String id) {
		for (ClientHandler handler : users) {
			if (handler.getUsername() == id) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * Checks if is name taken.
	 *
	 * @param userName the user name
	 * @return true, if is name taken
	 */
	public boolean isNameTaken(String userName) {
		for (ClientHandler handler : users) {
			if (userName.equals(handler.getUsername())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Users.
	 *
	 * @return the int
	 */
	public int users() {
		return users.size();
	}

	/**
	 * Adds the client handler.
	 *
	 * @param clientHandler the client handler
	 */
	public void addClientHandler(ClientHandler clientHandler) {
		users.add(clientHandler);
	}

	/**
	 * Removes the handler.
	 *
	 * @param clientHandler the client handler
	 */
	public void removeHandler(ClientHandler clientHandler) {
		if(game!=null && game.isGameRunning()) game.cleanUpAndUnexpectedEndGame();
		users.remove(clientHandler);
		validUsers--;
		
		if(validUsers==0) System.exit(0);
	}

	/**
	 * Gets the users id list.
	 *
	 * @return the users id list
	 */
	public ArrayList<String> getUsersIdList() {
		ArrayList<String> nameList = new ArrayList<String>();
		for (ClientHandler handler : users) {
			String name = handler.getUsername();
			nameList.add(name);
		}
		return nameList;
	}

	/**
	 * Send game info.
	 *
	 * @param userId the user id
	 * @param gameInfo the game info
	 * @throws PictionaryException the pictionary exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendGameInfo(String userId, String gameInfo) throws PictionaryException, IOException {
		ClientHandler handler = getClientHandlerById(userId);
		handler.sendMessageFromServerToClient("gameInfo", gameInfo);
	}

	/**
	 * Send message to client.
	 *
	 * @param username the username
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendMessageToClient(String username, String message) throws IOException {
		ClientHandler handler = getClientHandlerById(username);
		handler.sendMessageDirectlyToHandledClient(message);
	}

	/**
	 * Send broadcast message.
	 *
	 * @param senderUsername the sender username
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendBroadcastMessage(String senderUsername, String message) throws IOException {
		for (ClientHandler handler : users) {
			if (!handler.getUsername().equals(senderUsername)) {
				handler.sendMessageDirectlyToHandledClient(message);
			}
		}
	}

	/**
	 * Check word.
	 *
	 * @param word the word
	 * @param username the username
	 */
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

		} catch (IOException | ClassNotFoundException |PictionaryException exception) {
			System.out.println("Client connecetion failed");
			server.removeHandler(this);
			return;
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
