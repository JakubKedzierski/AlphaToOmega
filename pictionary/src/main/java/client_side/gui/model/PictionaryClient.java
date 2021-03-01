package client_side.gui.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import client_side.ClientApp;
import client_side.gui.view_controller.GameInterfaceController;
import lombok.Getter;
import lombok.Setter;
import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.PictionaryException;

/**
 * Client responsible for establishing connection with server and forwarding
 * messages/orders to app
 * 
 * Client will start establishing connection only if server is running,
 * otherwise throw an error. When connection is stable client is responsible for
 * client-server communication and sending parsed orders from server to view
 * controller or pictionary app.
 */
public class PictionaryClient implements Runnable {

	/** server default ip addres */
	public static String SERVER_NAME = "localhost";

	/** default port on which server is listening for connections */
	public static int SERVER_PORT = 25000;

	/** The connection timeout. */
	public static int CONNECTION_TIMEOUT = 2000;

	@Getter
	/** The username. */
	private String username;

	@Getter
	/** The socket through which communication is done. */
	private Socket socket = null;

	@Getter
	/** The output stream is used to get messages fromserver. */
	private ObjectInputStream inputStream = null;

	@Getter
	/** The input stream is used to send messages to server */
	private ObjectOutputStream outputStream = null;

	@Getter
	/** inform if client is connected to server, used to check if connection failed due to socket error or game end*/
	private boolean connected = false;

	@Getter
	/** inform is username validation is passed and user can start game */
	private boolean validUsername = false;


	@Setter
	/** View controller responsible for getting actions from user, executing client/server orders. */
	private GameInterfaceController guiController = null;

	/** Reference used for application callback - main purpose is to start game after name validation */
	private ClientApp app;

	/**
	 * Instantiates a new pictionary client.
	 *
	 * @param app Application
	 */
	public PictionaryClient(ClientApp app) {
		this.app = app;
	}

	/**
	 * Instantiates a new pictionary client.
	 */
	public PictionaryClient() {
	}

	/**
	 * Instantiates a new pictionary client / used in tests purpose
	 *
	 * @param name nickname
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PictionaryClient(String name) throws IOException {

		if (name != null)
			this.username = name;

		startClientConnection();
		validateName(name);
	}

	/**
	 * Starts client-server connection, initialize input/output stream used for communication.
	 *
	 * @throws IOException Signals that an I/O exception has occurred during establishing a connection.
	 */
	public void startClientConnection() throws IOException {
		socket = new Socket();
		SocketAddress addres = new InetSocketAddress(SERVER_NAME, SERVER_PORT);
		socket.connect(addres, CONNECTION_TIMEOUT);

		inputStream = new ObjectInputStream(socket.getInputStream());
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		connected = true;

		new Thread(this).start();
	}

	/**
	 * Username validation 
	 *
	 * @param username the username
	 */
	public void validateName(String username) {
		try {
			this.username = username;
			sendMessage("NameValidation", username, "server");
		} catch (PictionaryException | IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Main loop where client gets messages, handles connection errors
	 */
	@Override
	public void run() {
		try {
			while (true) {
				if (!socket.isConnected()) {
					System.out.println("Client " + username + "disconected");
					return;
				}
				String plainMessage;
				plainMessage = (String) inputStream.readObject();

				if (plainMessage != null && !plainMessage.equals("")) {
					parseProtocolMessage(plainMessage);
				}
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Wrong data type during comunication");

		} catch (IOException e) {
			if (!connected) {
				System.out.println("Client " + username + " disconected");
				return;
			} else {
				try {
					disconnect();
					System.out.println("Client " + username + " disconected");
				} catch (Exception e1) {
				}
				return;
			}

		} catch (PictionaryException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Parses the message which client received from server.
	 * 
	 * Parsed message is converted to specials orders and passed to view controller.
	 *
	 * @param plainMessage the plain message that client has received
	 * @throws JsonProcessingException throws when parsing goes wrong
	 * @throws PictionaryException     throws when parsing goes wrong
	 */
	public void parseProtocolMessage(String plainMessage) throws JsonProcessingException, PictionaryException {
		HashMap<PictionaryProtocolPool, String> messageInfo = PictionaryProtocolParser.parseProtocol(plainMessage);
		String messageType = messageInfo.get(PictionaryProtocolPool.MESSAGETYPE);
		String message = messageInfo.get(PictionaryProtocolPool.MESSAGE);

		switch (messageType) {

		case "chat":
				guiController.addMessage(messageInfo.get(PictionaryProtocolPool.SENDER) + ": "
						+ messageInfo.get(PictionaryProtocolPool.MESSAGE));
			
			break;

		case "gameInfo":
			if (message.equals("StartGame")) {
				app.startGame();
			}

			if (message.equals("host")) {
				guiController.setHostView();
			}

			if (message.equals("listener")) {
				guiController.setListenerView();
			}

			if (message.startsWith("round:")) {
				guiController.showRound(message.substring(6));
			}

			if (message.startsWith("word:")) {
				guiController.showWordToGuess(message.substring(5));
			}

			if (message.startsWith("goodGuess")) {
				guiController.goodGuessDone();
			}

			if (message.startsWith("game ended")) {
				String winner = message.substring(20);
				guiController.endGame(winner);
			}

			if (message.startsWith("points:")) {
				guiController.setPoints(Integer.parseInt(message.substring(7)));
			}

			if (message.startsWith("period:")) {
				int period = Integer.parseInt(message.substring(7, message.indexOf("/")));
				int numberOfPeriods = Integer.parseInt(message.substring(message.indexOf("/") + 1));
				guiController.setProgressBarValue(period, numberOfPeriods);
			}

			if (message.equals("round ended")) {
				guiController.cleanBoard();
			}

			break;

		case "pixelVector":
			if (message.equals("clear")) {
				guiController.cleanBoard();
				return;
			}
			String x = message.substring(0, message.indexOf(":"));
			String y = message.substring(message.indexOf(":") + 1, message.indexOf("|"));
			String size = message.substring(message.indexOf("|") + 1, message.indexOf("["));
			String color = message.substring(message.indexOf("[") + 1);
			guiController.drawImageFromHost(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(size),
					color);
			break;

		case "Error":
			break;

		case "NameValidation":
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("InvalidName")) {
				validUsername = false;
			}

			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("OK")) {
				validUsername = true;
			}
			break;

		default:
			throw new PictionaryException("Protocol wrapping failed");
		}

	}

	/**
	 *  Allows sending messages to server/choosen client/ broadcast - to each connected client to server
	 *
	 * @param messageType message type from pictionaryProtocolParser
	 * @param message     plain massage
	 * @param receiver    the receiver (receiver username) 
	 * @throws PictionaryException throws when parsing goes wrong
	 * @throws IOException        throws when connection is lost
	 */
	public void sendMessage(String messageType, String message, String receiver)
			throws PictionaryException, IOException {
		String jsonMessage = PictionaryProtocolParser.createProtocolMessage(username, receiver, messageType, message);
		outputStream.writeObject(jsonMessage);
	}

	/**
	 * Disconnect client and clean stuff.
	 */
	public void disconnect() {
		try {
			if (inputStream != null && !socket.isClosed())
				sendMessage("Error", "disconected", "server");

			Thread.sleep(400);
			if (socket != null)
				socket.close();
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			connected = false;
		} catch (IOException | PictionaryException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
