package server_side;

import java.io.IOException;


/**
 * 	Server-Handler communication interface
 */
public interface ServerHandlerInterface {
	
	/**
	 * Checks if server is in test mode.
	 *
	 * @return true, if is test mode
	 */
	public boolean isTestMode();
	
	/**
	 * Checks if is username is taken.
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
	 * Message is sent to each connected user
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
	 * Check if word guessed by player is proper.
	 * 
	 * This method is used while game is running
	 *
	 * @param word the word guessed by player
	 * @param username the username of guessing player
	 */
	public void checkWord(String word,String username);
}
