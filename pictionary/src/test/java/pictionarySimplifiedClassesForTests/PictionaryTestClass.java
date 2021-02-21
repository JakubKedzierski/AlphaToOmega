package pictionarySimplifiedClassesForTests;

import lombok.Getter;
import server_side.GameCommunication;
import server_side.pictionary.Pictionary;

public class PictionaryTestClass extends Pictionary {
	
	private @Getter boolean roundEnded=false;
	
	public PictionaryTestClass(GameCommunication server) {
		super(server,4);
	}
	
	@Override
	public void roundEnded() {
		roundEnded=true;
	}

	
}
