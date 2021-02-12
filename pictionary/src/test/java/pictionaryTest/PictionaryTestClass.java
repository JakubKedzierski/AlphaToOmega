package pictionaryTest;

import lombok.Getter;
import server_side.GameCommunication;
import server_side.pictionary.Pictionary;

public class PictionaryTestClass extends Pictionary {
	
	private @Getter boolean roundEnded=false;
	
	public PictionaryTestClass(GameCommunication server) {
		super(server);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void roundEnded() {
		roundEnded=true;
	}

	
}
