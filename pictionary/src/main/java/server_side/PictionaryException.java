package server_side;


/**
 * Project main exception.
 */
public class PictionaryException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Instantiates a new pictionary exception.
	 */
	PictionaryException(){}
	
	/**
	 * Instantiates a new pictionary exception.
	 *
	 * @param errorMessage the error message
	 */
	public PictionaryException(String errorMessage){
		super(errorMessage);
	}
	

	
	
}
