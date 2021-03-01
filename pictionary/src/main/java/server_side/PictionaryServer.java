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

/**
 * Server class that handles client connections, takes care of game progress.
 * 
 * Server shut down when last user disconnect (check method <code>removeHandler</code>). Server is run on default user
 * address:localhost on 25000 port. This server class only accepts connections
 * with clients, handling connections is moved to private <code>ClientHandler</code>
 * class. 
 */
public class PictionaryServer implements Runnable, GameCommunication, ServerHandlerInterface {

	/** server starts on this port. */
	public static int SERVER_PORT = 25000;

	/** Number of players to start game. */
	private int playersToStartGame = 2;

	@Getter
	/** Trace if server is running. */
	private boolean running = false;

	@Getter
	/** List of handlers of users that are connected to server */
	private ConcurrentLinkedQueue<ClientHandler> users = new ConcurrentLinkedQueue<ClientHandler>();

	@Getter
	/** Number of users that already passed through username validation. */
	private int validUsers = 0;

	private Pictionary game = null;
	private ServerSocket serverSocket = null;

	/** Amount of already accepted connections. */
	private int acceptedConnections = 0;

	@Getter
	/**
	 * Mode of server: normal:false  | test mode: true
	 * 
	 * This variable is used for test purpose to not start game
	 */
	private boolean testMode = false;

	/**
	 * Instantiates a new pictionary server and starts main thread.
	 *
	 * @param playersToStartGame number of players to start game
	 * @param testMode           the test mode: true-normal, false-test mode
	 */
	public PictionaryServer(int playersToStartGame, boolean testMode) {
		this.playersToStartGame = playersToStartGame;
		this.testMode = testMode;
		new Thread(this).start();
	}

	/**
	 * Instantiates a new pictionary server.
	 * 
	 * Starts server main thread
	 */
	public PictionaryServer() {
		new Thread(this).start();
	}

	/**
	 * The main method.
	 *
	 * @param args no use of args in app
	 */
	public static void main(String[] args) {
		new PictionaryServer();

	}

	/**
	 * listening loop, accepting new connections
	 *
	 * @throws IOException is thrown when socket error occures
	 */
	private void listeningLoop() throws IOException {
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
	 *  Starting server
	 */
	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(SERVER_PORT);
			int port = serverSocket.getLocalPort();
			String address = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Server starts on port  " + port);
			System.out.println("Host address: " + address);

			if (!testMode)
				game = new Pictionary(this, playersToStartGame);

			listeningLoop();

		} catch (IOException ioException) {

			if (running)
				return;
			else
				disconnectServer();

			throw new IllegalArgumentException("Probably another server is listening on this port.");
		}

	}

	/**
	 * Adding user to game. When all players are connected, <code>startGame</code> is called
	 *
	 * @param name username of new user
	 */
	public void addUserToGame(String name) {
		game.addUser(name);
		validUsers++;
		if (validUsers == playersToStartGame) {
			startGame();
		}
	}

	/**
	 * Start pictionary game.
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
	 * Disconnect server and ends any connections with users.
	 */
	public void disconnectServer() {
		running = true;
		try {

			for (ClientHandler handler : users) {
				handler.diconnectClient();
			}

			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the client handler by username.
	 *
	 * @param id username 
	 * @return ClientHanlder which takes care of proper user is returned
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
	 * @param userName proposed username
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
	 * Get number of client handlers that are running
	 *
	 * @return number of users (clients handlers) that are running
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
	 * Removes the handler if number of handler is 0, server shut down
	 *
	 * @param clientHandler the client handler
	 */
	public void removeHandler(ClientHandler clientHandler) {
		if (game != null && game.isGameRunning())
			game.cleanUpAndUnexpectedEndGame();
		users.remove(clientHandler);
		validUsers--;

		if (validUsers == 0)
			System.exit(0);
	}

	/**
	 * Gets list of usernames (ask each handler for username)
	 *
	 * @return the users username list
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
	 * Send game info/stage game info.
	 * 
	 * This method is used by game class to inform client about game status, new stage etc.
	 *
	 * @param username  username of player to send message
	 * @param gameInfo game information
	 * @throws PictionaryException the pictionary exception thrown when parsing message goes wrong
	 * @throws IOException  if socket is closed IOException is thrown
	 */
	public void sendGameInfo(String username, String gameInfo) throws PictionaryException, IOException {
		ClientHandler handler = getClientHandlerById(username);
		handler.sendMessageFromServerToClient("gameInfo", gameInfo);
	}

	/**
	 * Send server message to client.
	 *
	 * @param username username of player to send message
	 * @param message  message to be sent
	 * @throws IOException if socket is closed IOException is thrown
	 */
	public void sendMessageToClient(String username, String message) throws IOException {
		ClientHandler handler = getClientHandlerById(username);
		handler.sendMessageDirectlyToHandledClient(message);
	}

	/**
	 * Send broadcast message to each user (every handler send message to user that is handled)
	 *
	 * @param senderUsername sender username
	 * @param message   message to be sent
	 * @throws IOException if socket is closed IOException is thrown
	 */
	public void sendBroadcastMessage(String senderUsername, String message) throws IOException {
		for (ClientHandler handler : users) {
			if (!handler.getUsername().equals(senderUsername)) {
				handler.sendMessageDirectlyToHandledClient(message);
			}
		}
	}

	/**
	 * Checking if word guessed by user is proper
	 *
	 * @param word     guessing word
	 * @param username username of guessing player
	 */
	@Override
	public void checkWord(String word, String username) {
		game.checkWord(word, username);
	}

}

/**
 *  Handling user connections, forwarding messages, checking connection status
 *
 */
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
	
	/**
	 *  main loop where handler receives and parses messages and inits name validation
	 */
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

		} catch (IOException | ClassNotFoundException | PictionaryException exception) {
			System.out.println("Client connecetion failed");
			server.removeHandler(this);
			return;
		}

	}

	/**
	 * processing name validation, sending validation messages to user
	 * 
	 * @param userDeclaredName name declared by user
	 * @throws PictionaryException thrown when wrapping goes wrong
	 * @throws IOException thrown when connections is lost
	 */
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
				if (!server.isTestMode())
					server.addUserToGame(userDeclaredName);
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
			exception.printStackTrace();
			throw new PictionaryException("Processing exception");
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
