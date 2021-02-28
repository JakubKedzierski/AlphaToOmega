package server_side;

import java.io.IOException;
// TODO: Auto-generated Javadoc

/**
 * The Interface ServerHandlerInterface.
 */
public interface ServerHandlerInterface {
	
	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	public boolean isTestMode();
	
	/**
	 * Checks if is name taken.
	 *
	 * @param userName the user name
	 * @return true, if is name taken
	 */
	public boolean isNameTaken(String userName);
	
	/**
	 * Removes the handler.
	 *
	 * @param clientHandler the client handler
	 */
	public void removeHandler(ClientHandler clientHandler);
	
	/**
	 * Send message to client.
	 *
	 * @param username the username
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendMessageToClient(String username,String message) throws IOException;
	
	/**
	 * Send broadcast message.
	 *
	 * @param senderUsername the sender username
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendBroadcastMessage(String senderUsername,String message) throws IOException;
	
	/**
	 * Adds the user to game.
	 *
	 * @param name the name
	 */
	public void addUserToGame(String name);
	
	/**
	 * Check word.
	 *
	 * @param word the word
	 * @param username the username
	 */
	public void checkWord(String word,String username);
}
