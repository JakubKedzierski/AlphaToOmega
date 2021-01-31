package pictionary;

public class PictionaryClientException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public PictionaryClientException() {}
	
	public PictionaryClientException(String errorMessage) {
		super(errorMessage);
	}

}
