package pictionary;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PictionaryServer implements Runnable {
	public static int SERVER_PORT=25000;
	Map<ClientHandler, Integer> map=new HashMap<ClientHandler, Integer>();
	int userCount=0;
	
	
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
					newConnectionStartup(clientSocket);
				}
				
			}
			
			while(true) {}  // to keep thread alive
			
		}catch(IOException ioException) {
			System.out.println("Issues with listening thread");
		}
		
	}
		
	private void newConnectionStartup(Socket clientSocket) {
		
	}
	

}


class ClientHandler{
	int userId;
	
	ClientHandler(){
		
	}
	
}