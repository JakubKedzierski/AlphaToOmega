package pictionary;

public interface GameCommunication {
	
	public void sendHostInfo(String userId,String word); // send  info you are the host
	public void sendListenerInfo(String userId); // send info you are listener
	public void sendEndGameInfo(); // to every user
	
}
