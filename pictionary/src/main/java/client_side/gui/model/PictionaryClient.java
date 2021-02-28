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

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryClient.
 */
public class PictionaryClient implements Runnable {

	/** The server name. */
	public static String SERVER_NAME = "localhost";
	
	/** The server port. */
	public static int SERVER_PORT = 25000;
	
	/** The connection timeout. */
	public static int CONNECTION_TIMEOUT=2000;

	/** The username. */
	private /**
  * Gets the username.
  *
  * @return the username
  */
 @Getter String username;
	
	/** The socket. */
	private 
 /**
  * Gets the socket.
  *
  * @return the socket
  */
 @Getter Socket socket = null;
	
	/** The input stream. */
	private 
 /**
  * Gets the input stream.
  *
  * @return the input stream
  */
 @Getter ObjectInputStream inputStream = null;
	
	/** The output stream. */
	private 
 /**
  * Gets the output stream.
  *
  * @return the output stream
  */
 @Getter ObjectOutputStream outputStream = null;
	
	/** The connected. */
	private 
 /**
  * Checks if is connected.
  *
  * @return true, if is connected
  */
 @Getter boolean connected = false;
	
	/** The valid username. */
	private 
 /**
  * Checks if is valid username.
  *
  * @return true, if is valid username
  */
 @Getter boolean validUsername = false;
	
	/** The client type. */
	private 
 /**
  * Checks if is client type.
  *
  * @return true, if is client type
  */
 @Getter boolean clientType = false; // false graphical/true test
	
	/** The gui controller. */
	private 
 /**
  * Sets the gui controller.
  *
  * @param guiController the new gui controller
  */
 @Setter GameInterfaceController guiController = null;
	
	/** The app. */
	private ClientApp app;

	/**
	 * Instantiates a new pictionary client.
	 *
	 * @param app the app
	 */
	public PictionaryClient(ClientApp app) {
		this.app = app;
		clientType = false;
	}
	
	/**
	 * Instantiates a new pictionary client.
	 */
	public PictionaryClient() {}

	/**
	 * Instantiates a new pictionary client.
	 *
	 * @param name the name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PictionaryClient(String name) throws IOException {
		clientType = true;
		
		if (name != null)
			this.username = name;

		startClientConnection();
		validateName(name);
	}

	/**
	 * Start client connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void startClientConnection() throws IOException {
		socket = new Socket();
		SocketAddress addres = new InetSocketAddress(SERVER_NAME, SERVER_PORT);
		socket.connect(addres,CONNECTION_TIMEOUT);
		
		inputStream = new ObjectInputStream(socket.getInputStream());
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		connected = true;

		new Thread(this).start();
	}

	/**
	 * Validate name.
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
	 * Run.
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

		} catch ( ClassNotFoundException e) {
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
	 * Parses the protocol message.
	 *
	 * @param plainMessage the plain message
	 * @throws JsonProcessingException the json processing exception
	 * @throws PictionaryException the pictionary exception
	 */
	public void parseProtocolMessage(String plainMessage) throws JsonProcessingException, PictionaryException {
		HashMap<PictionaryProtocolPool, String> messageInfo = PictionaryProtocolParser.parseProtocol(plainMessage);
		String messageType = messageInfo.get(PictionaryProtocolPool.MESSAGETYPE);
		String message = messageInfo.get(PictionaryProtocolPool.MESSAGE);
		

		switch (messageType) {

		case "chat":
			if (clientType) {
				System.out.println("Client: " + username + "| Received message: " + " "
						+ messageInfo.get(PictionaryProtocolPool.MESSAGE));
			} else {
				guiController.addMessage(messageInfo.get(PictionaryProtocolPool.SENDER) + ": "
						+ messageInfo.get(PictionaryProtocolPool.MESSAGE));
			}
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
			
			if(message.startsWith("points:")) {
				guiController.setPoints(Integer.parseInt(message.substring(7)));
			}
			
			if(message.startsWith("period:")) {
				int period = Integer.parseInt(message.substring(7,message.indexOf("/")));
				int numberOfPeriods = Integer.parseInt(message.substring(message.indexOf("/")+1));
				guiController.setProgressBarValue(period, numberOfPeriods);
			}
			
			if(message.equals("round ended")) {
				guiController.cleanBoard();
			}
			
			
			break;

		case "pixelVector":
			if(message.equals("clear")) {
				guiController.cleanBoard(); return;
			}
			String x =message.substring(0, message.indexOf(":"));
			String y = message.substring(message.indexOf(":") + 1,message.indexOf("|"));
			String size = message.substring(message.indexOf("|") + 1,message.indexOf("["));
			String color =  message.substring(message.indexOf("[") + 1);
			guiController.drawImageFromHost(Double.parseDouble(x),Double.parseDouble(y),Double.parseDouble(size),color);
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
	 * Send message.
	 *
	 * @param messageType the message type
	 * @param message the message
	 * @param receiver the receiver
	 * @throws PictionaryException the pictionary exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendMessage(String messageType, String message, String receiver)
			throws PictionaryException, IOException {
		String jsonMessage = PictionaryProtocolParser.createProtocolMessage(username, receiver, messageType, message);
		outputStream.writeObject(jsonMessage);
	}

	/**
	 * Disconnect.
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
