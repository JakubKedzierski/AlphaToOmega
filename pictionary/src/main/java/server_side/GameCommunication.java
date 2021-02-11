package server_side;

import java.io.IOException;

public interface GameCommunication {
	
	public void startGame();
	public void sendHostInfo(String userId,String word) throws PictionaryException, IOException; // send  info you are the host
	public void sendListenerInfo(String userId) throws PictionaryException, IOException; // send info you are listener
	public void sendEndGameInfo() throws PictionaryException, IOException; // to every user
	
}
