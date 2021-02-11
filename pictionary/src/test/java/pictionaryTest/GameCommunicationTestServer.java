package pictionaryTest;

import server_side.GameCommunication;

public class GameCommunicationTestServer implements GameCommunication{

	@Override
	public void sendListenerInfo(String userId) {
		System.out.println("Sent listener info to " + userId);
	}


	@Override
	public void sendEndGameInfo() {
		System.out.println("Sent end game info to everyone");
	}

	@Override
	public void sendHostInfo(String userId, String word) {
		System.out.println("Sent host info to " +userId + " and gueess word: " + word);
		
	}


	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	
}
