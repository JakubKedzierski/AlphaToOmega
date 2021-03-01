package server_side;

import java.io.IOException;

/**
 *  Server imitations that alows game to make callbacks 
 */
public interface GameCommunication {
	
	/**
	 * Start game.
	 */
	public void startGame();
	
	/**
	 * Send game info/status.
	 *
	 * @param userId username
	 * @param gameInfo the game info
	 * @throws PictionaryException the pictionary exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendGameInfo(String userId,String gameInfo) throws PictionaryException, IOException; 
	
}
