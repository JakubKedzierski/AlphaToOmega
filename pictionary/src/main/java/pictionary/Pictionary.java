package pictionary;

import java.util.ArrayList;


public class Pictionary{
	private String[] wordDataBase={"rabbit", "house" , "river", "shrek"};
	public static int NUMBER_OF_PLAYERS=4;
	ArrayList<PictionaryPlayer> users;
	GameCommunication server;
	int roundCount;
	PictionaryRound round=null;
	
	
	
	public Pictionary(GameCommunication server){
		this.server=server;
		this.users=new ArrayList<PictionaryPlayer>();
		roundCount=0;
	}
	
	public void addUser(String name) {
		PictionaryPlayer player= new PictionaryPlayer(name);
		users.add(player);
	}
	
	public void deleteUser(String name) {
		for(PictionaryPlayer user:users) {
			if(user.getName().equals(name)) {
				users.remove(user);
			}
		}
	}
	
	public void startGame() {
		if(users.size()==NUMBER_OF_PLAYERS && server!=null) {
			newRound();
		}else {
			throw new IllegalArgumentException("More users needed to start a game");
		}
	}
	
	private void newRound() {
		chooseNewHost();
		round=new PictionaryRound(wordDataBase[roundCount],this);
		roundCount++;
	}

	private void chooseNewHost() {
		int host=roundCount;
		server.sendHostInfo(users.get(host).getName());
		for(int i=0;i<NUMBER_OF_PLAYERS;i++) {
			if(i!=host) server.sendListenerInfo(users.get(i).getName());
		}
		server.sendGuessingWord(wordDataBase[host]);
	}
	
	public void checkWord(String word) {
		
		if(round.guessedWord(word)) {
			
		}else {
			
		}
	}
	
	public void roundEnded() {
		
	}
	
}
