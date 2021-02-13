package client_side.gui.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import javax.management.RuntimeErrorException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import client_side.PictionaryClientApp;
import client_side.gui.view_controller.GameInterfaceController;
import lombok.Getter;
import lombok.Setter;
import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.PictionaryException;
import server_side.pictionary.PictionaryPlayer;

public class PictionaryClient implements Runnable {

	public static String SERVER_NAME = "localhost";
	public static int SERVER_PORT = 25000;

	private @Getter String username;
	private @Getter Socket socket = null;
	private @Getter ObjectInputStream inputStream = null;
	private @Getter ObjectOutputStream outputStream = null;
	private @Getter boolean connected = false;
	private @Getter boolean validUsername = false;
	private @Getter boolean clientType = false; // false graphical/true test
	private @Setter GameInterfaceController guiController = null;
	private ClientApp app;

	public PictionaryClient(ClientApp app) {
		this.app=app;
		clientType = false;
	}

	public PictionaryClient(GameInterfaceController guiController) {
		this.guiController = guiController;
		clientType = false;
	}

	public PictionaryClient(String name, GameInterfaceController guiController) {
		this.guiController = guiController;
		if (name != null)
			this.username = name;
		clientType = false;
		try {
			startClientConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PictionaryClient(String name, boolean testClient) {
		clientType = testClient;
		if (clientType == false)
			throw new IllegalArgumentException("Start client gui first");

		if (name != null)
			this.username = name;

		try {
			startClientConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startClientConnection() throws IOException {
		socket = new Socket(SERVER_NAME, SERVER_PORT);
		inputStream = new ObjectInputStream(socket.getInputStream());
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		connected = true;

		new Thread(this).start();
	}
	
	public void validateName(String username) {
		try {
			this.username=username;
			sendMessage("NameValidation", username, "server");
		} catch (PictionaryException | IOException exception) {
			exception.printStackTrace();
		}
	}

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

		} catch (JacksonException | ClassNotFoundException e) {
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

	public void parseProtocolMessage(String plainMessage) throws JacksonException, PictionaryException {
		HashMap<PictionaryProtocolPool, String> messageInfo = PictionaryProtocolParser.parseProtocol(plainMessage);
		String messageType = messageInfo.get(PictionaryProtocolPool.MESSAGETYPE);
		System.out.println(plainMessage);

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
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("StartGame")) {
				app.startGame();
			}
			
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("host")) {
				guiController.setHostView();
			}
			
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("listener")) {
				guiController.setListenerView();
			}
			
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).startsWith("round:")) {
				guiController.showRound(messageInfo.get(PictionaryProtocolPool.MESSAGE).substring(6));
			}
			
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).startsWith("word:")) {
				guiController.showWordToGuess(messageInfo.get(PictionaryProtocolPool.MESSAGE).substring(5));
			}
			
			if (messageInfo.get(PictionaryProtocolPool.MESSAGE).equals("game ended")) {
				guiController.endGame();
			}
			
			
			break;

		case "pixelVector":
			break;

		case "guessedWord":
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


	public void sendMessage(String messageType, String message, String receiver)
			throws PictionaryException, IOException {
		String jsonMessage = PictionaryProtocolParser.createProtocolMessage(username, receiver, messageType, message);
		outputStream.writeObject(jsonMessage);
	}

	public void disconnect() {
		try {
			if (inputStream != null)
				sendMessage("Error", "disconected", "server");
			if (socket != null)
				socket.close();
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			connected = false;
		} catch (IOException | PictionaryException e) {

		}
	}

}
