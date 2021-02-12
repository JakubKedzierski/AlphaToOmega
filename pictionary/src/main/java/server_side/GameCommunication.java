package server_side;

import java.io.IOException;

public interface GameCommunication {
	
	public void startGame();
	public void sendGameInfo(String userId,String gameInfo) throws PictionaryException, IOException; 
	
}
