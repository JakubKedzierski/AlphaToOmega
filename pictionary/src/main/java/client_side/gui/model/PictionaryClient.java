package client_side.gui.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import client_side.gui.view_controller.GameInterfaceController;
import lombok.Getter;
import lombok.Setter;
import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.PictionaryException;

public class PictionaryClient implements Runnable {

	public static String SERVER_NAME = "localhost";
	public static int SERVER_PORT = 25000;
	public static int CONNECTION_TIMEOUT=2000;

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
		this.app = app;
		clientType = false;
	}
	
	public PictionaryClient() {}

	public PictionaryClient(String name) throws IOException {
		clientType = true;
		
		if (name != null)
			this.username = name;

		startClientConnection();
		validateName(name);
	}

	public void startClientConnection() throws IOException {
		socket = new Socket();
		SocketAddress addres = new InetSocketAddress(SERVER_NAME, SERVER_PORT);
		socket.connect(addres,CONNECTION_TIMEOUT);
		
		inputStream = new ObjectInputStream(socket.getInputStream());
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		connected = true;

		new Thread(this).start();
	}

	public void validateName(String username) {
		try {
			this.username = username;
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

	public void sendMessage(String messageType, String message, String receiver)
			throws PictionaryException, IOException {
		String jsonMessage = PictionaryProtocolParser.createProtocolMessage(username, receiver, messageType, message);
		outputStream.writeObject(jsonMessage);
	}

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
