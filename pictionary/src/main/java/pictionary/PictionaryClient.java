package pictionary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

			outputStream.writeObject(name);

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
			return;
			
		} catch (ClassNotFoundException e) {
			System.out.println("Wrong data type during comunication");
		
		} catch (PictionaryClientException e) {
			System.out.println(e.getMessage());
		}

	}

	public void parseProtocolMessage(String plainMessage) throws JacksonException, PictionaryClientException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode message = mapper.readTree(plainMessage);
		String messageType=message.path("messageType").asText();
		switch(messageType){
		
			case "Error":
				if(message.path("message").asText().equals("NameValidation")) {
					resolveNameValidation();
				}
				
				break;
				
			default:
				throw new PictionaryClientException("Protocol wrapping failed");
		}

	}
	
	private void resolveNameValidation() {
		
		System.out.println("name validation start");
		// get new name
		
	}

}
