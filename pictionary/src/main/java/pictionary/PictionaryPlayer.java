package pictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;


@EqualsAndHashCode
public class PictionaryPlayer {
	private @Getter String  name;
	
	public PictionaryPlayer(String name) {
		this.name=name;
	}
	

}
