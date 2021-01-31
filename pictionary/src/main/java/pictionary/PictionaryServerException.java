package pictionary;

public class PictionaryServerException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public PictionaryServerException() {}
	
	public PictionaryServerException(String errorMessage) {
		super(errorMessage);
	}

}
