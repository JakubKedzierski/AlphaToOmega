package protocol_parser;

/**
 *  Proper client/server communication message have to contain each of these pools
 */
public enum PictionaryProtocolPool {

	/** The sender. */
	SENDER(),
	/** The receiver. */
	RECEIVER(),
	/** The messagetype. */
	MESSAGETYPE(),
	/** The message. */
	MESSAGE();
}