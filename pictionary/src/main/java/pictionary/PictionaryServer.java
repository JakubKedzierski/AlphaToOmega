package pictionary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PictionaryServer implements Runnable {
	public static int SERVER_PORT=25000;
	private List<ClientHandler> users=new ArrayList<ClientHandler>();
	private int userCount=0;
	
	
	PictionaryServer(){
		new Thread(this).start();
	}
	
	
	public static void main(String[] args) {
		new PictionaryServer();

	}


	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
			int port=serverSocket.getLocalPort();
			String address=InetAddress.getLocalHost().getHostAddress();
			System.out.println("Server starts on port  "+ port);
			System.out.println("Host address: "+ address);
			
			while(userCount<4) {
				Socket clientSocket = serverSocket.accept();
				if(clientSocket!=null) {
					ClientHandler userHandler=new ClientHandler(this,clientSocket);
					users.add(userHandler);
					userCount++;
				}
				
			}
			
			while(true) {}  // to keep thread alive
			
		}catch(IOException ioException) {
			System.out.println("Issues with listening thread");
		}
		
	}
	
	public ClientHandler getClientHandlerById(final String id) {
		for(ClientHandler handler:users) {
			if(handler.getUserId()==id) {
				return handler;
			}
		}
		return null;
	}
	
	public void addClientHandler(ClientHandler clientHandler) {
		users.add(clientHandler);
	}

	public void removeHandler(ClientHandler clientHandler) {
		users.remove(clientHandler);
		userCount--;
	}
	
}

class ClientHandler implements Runnable{
	
	private String userId=null;
	private PictionaryServer server=null;
	private Socket socket=null;
	private ObjectInputStream inputStream=null;
	private ObjectOutputStream outputStream=null;
	
	ClientHandler(PictionaryServer server,Socket socket){
		this.server=server;
		this.socket=socket;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());) {
			
			outputStream = output;
			inputStream = input;
			
			String message=(String)input.readObject();
			
			if(message!=null) {
				try{
					newConnectionStartup(message);
				}catch(IOException fatalException) {
					server.removeHandler(this);
					return;
				}
			}
			
			while(true) {

			}
			
		}catch(Exception exception) {
			
		}
		
	}

	private void newConnectionStartup(String message) throws IOException{

	    ObjectMapper mapper = new ObjectMapper();

			JsonNode greetingMessage = mapper.readTree(message);
			if (greetingMessage.has("name")) {
			    userId=greetingMessage.path("name").asText();
			}else {
				throw new IOException("Message dosent contain name attribute.");
			}

	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return "Handler of" + userId + "\n";
		
	}
	
}

