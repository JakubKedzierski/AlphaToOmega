package client_side;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;
import pictionary.pictionaryProtocolParser;
import pictionary.pictionaryProtocolPool;

public class PictionaryClient implements Runnable {

	public static String SERVER_NAME = "localhost";
	public static int SERVER_PORT = 25000;

	private @Getter @Setter String name;
	private @Getter Socket socket = null;
	private @Getter ObjectInputStream inputStream = null;
	private @Getter ObjectOutputStream outputStream = null;
	private @Getter boolean connected = false;

	public static void main(String[] args) {
		new PictionaryClient("test");
	}
	

	
	public PictionaryClient(String name) {
		if (name != null)
			this.name = name;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			socket = new Socket(SERVER_NAME, SERVER_PORT);
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			connected = true;
			sendNameToServer(name);

			while (true) {
				if (!connected) {
					System.out.println("Client " + name + "disconected");
					return;
				}
				String plainMessage;
				plainMessage = (String) inputStream.readObject();

				if (plainMessage != null && !plainMessage.equals("")) {
					parseProtocolMessage(plainMessage);
				}
			}

		} catch (JacksonException exception) {
			System.out.println("Wrong data type during comunication");

		} catch (UnknownHostException e) {
			System.out.println("Failed to connect with server");
			return;

		} catch (IOException e) {
			if (!connected) {
				System.out.println("Client " + name + " disconected");
				return;
			} else {
				try {
					disconnect();
					System.out.println("Client " + name + " disconected");
				} catch (Exception e1) {
				}
				return;
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Wrong data type during comunication");

		} catch (PictionaryClientException e) {
			System.out.println(e.getMessage());
		}

	}

	public void parseProtocolMessage(String plainMessage) throws JacksonException, PictionaryClientException {
		HashMap<pictionaryProtocolPool, String> messageInfo = pictionaryProtocolParser.parseProtocol(plainMessage);
		String messageType = messageInfo.get(pictionaryProtocolPool.MESSAGETYPE);

		switch (messageType) {

		case "chat":
			receivedMessage(messageInfo.get(pictionaryProtocolPool.MESSAGE));
			break;

		case "pixelVector":
			break;

		case "guessedWord":
			break;

		case "Error":
			if (messageInfo.get(pictionaryProtocolPool.MESSAGE).equals("NameValidation")) {
				resolveNameValidation();
			}

			break;

		default:
			throw new PictionaryClientException("Protocol wrapping failed");
		}

	}

	private void resolveNameValidation() throws PictionaryClientException {

		System.out.println("name validation start");

		// get new name

		sendNameToServer("newName");

	}

	public void sendNameToServer(String name) throws PictionaryClientException {
		sendMessage("NameValidation", name, "server");
	}

	public void sendMessage(String messageType, String message, String receiver) throws PictionaryClientException {
		final String[] dataTypes = { "chat", "pixelVector", "guessedWord", "Error", "NameValidation" };

		if (!Arrays.stream(dataTypes).anyMatch(messageType::equals)) {
			throw new IllegalArgumentException("Wrong message type");
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.createObjectNode();
			((ObjectNode) node).put("sender", name);
			((ObjectNode) node).put("receiver", receiver);
			((ObjectNode) node).put("messageType", messageType);
			((ObjectNode) node).put("message", message);
			String validateMessage = mapper.writeValueAsString(node);
			sendMessageToServer(validateMessage);

		} catch (JsonProcessingException exception) {
			throw new PictionaryClientException("JSON wrapping went wrong");
		}
	}

	private void sendMessageToServer(String message) throws PictionaryClientException {
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			throw new PictionaryClientException("Message sending failed");
		}
	}

	public void disconnect() throws PictionaryClientException, IOException {
		sendMessage("Error", "disconected", "server");
		if (socket != null)
			socket.close();
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
		connected = false;
	}

	public void receivedMessage(String message) {
		System.out.println("Client: " + name +"| Received message: " + " " +  message);
	}
}
