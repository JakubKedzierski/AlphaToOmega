package pictionary;

public interface GameCommunication {
	
	public void sendHostInfo(String userId);
	public void sendListenerInfo(String userId);
	public void sendGuessingWord(String word);
	
}
