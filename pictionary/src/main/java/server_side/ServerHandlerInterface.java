package server_side;

import java.io.IOException;
public interface ServerHandlerInterface {
	
	public boolean isNameTaken(String userName);
	public void removeHandler(ClientHandler clientHandler);
	public void sendMessageToClient(String username,String message) throws IOException;
	public void sendBroadcastMessage(String senderUsername,String message) throws IOException;
	public void addUserToGame(String name);
	public void checkWord(String word,String username);
}
