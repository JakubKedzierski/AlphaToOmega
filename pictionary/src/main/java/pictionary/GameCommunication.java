package pictionary;

import server_side.PictionaryServerException;

public interface GameCommunication {
	
	public void sendHostInfo(String userId,String word) throws PictionaryServerException; // send  info you are the host
	public void sendListenerInfo(String userId) throws PictionaryServerException; // send info you are listener
	public void sendEndGameInfo() throws PictionaryServerException; // to every user
	
}
