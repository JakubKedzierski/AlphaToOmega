package pictionary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;

public class PictionaryClient implements Runnable {

	public static String SERVER_NAME = "localhost";
	public static int SERVER_PORT = 25000;

	private @Getter @Setter String name;
	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;

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
			sendNameToServer(name);

			while (true) {
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
			System.out.println("Failed to connect with server");

		} catch (ClassNotFoundException e) {
			System.out.println("Wrong data type during comunication");

		} catch (PictionaryClientException e) {
			System.out.println(e.getMessage());
		}

	}

	public void parseProtocolMessage(String plainMessage) throws JacksonException, PictionaryClientException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode message = mapper.readTree(plainMessage);
		String messageType = message.path("messageType").asText();
		switch (messageType) {
		
		case "chat":
			break;

		case "pixelVector":
			break;

		case "guessedWord":
			break;

		case "Error":
			if (message.path("message").asText().equals("NameValidation")) {
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
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.createObjectNode();
			((ObjectNode) node).put("name", name);
			String nameValidationMessage = mapper.writeValueAsString(node);
			sendMessageToServer(nameValidationMessage);

		} catch (JsonProcessingException exception) {
			System.out.println("JSON wrapping went wrong");
			throw new PictionaryClientException();
		} catch (IOException e) {
			throw new PictionaryClientException();
		}
	}

	public void sendMessage(String messageType, String message, String receiver) throws PictionaryClientException {
		final String[] dataTypes = { "chat", "pixelVector", "guessedWord", "Error" };

		if (!Arrays.stream(dataTypes).anyMatch(messageType::equals)) {
			return;
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

	public void sendMessageToServer(String message) throws PictionaryClientException {
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			throw new PictionaryClientException("Message sending failed");
		}
	}

}