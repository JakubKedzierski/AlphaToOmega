package server_side;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Interface GameCommunication.
 */
public interface GameCommunication {
	
	/**
	 * Start game.
	 */
	public void startGame();
	
	/**
	 * Send game info.
	 *
	 * @param userId the user id
	 * @param gameInfo the game info
	 * @throws PictionaryException the pictionary exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendGameInfo(String userId,String gameInfo) throws PictionaryException, IOException; 
	
}
