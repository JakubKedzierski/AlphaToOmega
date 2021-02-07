package pictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;


@EqualsAndHashCode
public class PictionaryPlayer {
	private @Getter String  name;
	private @Getter int points;
	
	public PictionaryPlayer(String name) {
		this.name=name;
		this.points=0;
	}
	
	public void addPoints(int points) {
		this.points+=points;
	}
	
	public void subPoints(int points) {
		this.points-=points;
	}
	

}
