package server_side.pictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
public class PictionaryPlayer {
	private @Getter String  name = null;
	private @Getter @Setter String typeOfPlayer = null;
	private @Getter @Setter boolean goodGuessAlreadyDone = false; // to check if user already guess the word
	private @Getter int points=0;
	
	public PictionaryPlayer(String name) {
		this.name=name;
	}
	
	public PictionaryPlayer(String name, String typeOfPlayer) {
		this.name=name;
		this.typeOfPlayer=typeOfPlayer;
	}
	
	public void addPoints(int points) {
		this.points+=points;
	}
	
	public void subPoints(int points) {
		this.points-=points;
	}
	

}
