package client_side;


/**
 *  Interface in the purpose of PictionaryClientApp and PictionaryClient communication and making callbacks.
 *  Client do not need a reference to all of app methods but only to specific one.
 *  
 *  Client use GuiController to communicate with user interface.
 */
public interface ClientApp {
	
	/**
	 *  Loading new scene for UI, setting UI controller and starting pictionary game on client side.
	 */
	public void startGame();
}
