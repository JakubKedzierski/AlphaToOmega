package protocol_parser;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import server_side.PictionaryException;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryProtocolParser.
 */
public final class PictionaryProtocolParser {
	
	/** The Constant protocolPool. */
	private static final String[] protocolPool = { "sender", "receiver", "messageType", "message" };
	
	/** The Constant messageTypes. */
	private static final String[] messageTypes = { "chat", "pixelVector", "guessedWord", "Error", "NameValidation",
			"gameInfo" };

	/**
	 * Parses the protocol.
	 *
	 * @param jsonMessage the json message
	 * @return the hash map
	 * @throws JsonProcessingException the json processing exception
	 */
	public static HashMap<PictionaryProtocolPool, String> parseProtocol(String jsonMessage) throws JsonProcessingException  {
		HashMap<PictionaryProtocolPool, String> parsedMessage = new HashMap<PictionaryProtocolPool, String>();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
		mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode messageNode = mapper.readTree(jsonMessage);

		for (String pool : protocolPool) {
			if (!messageNode.hasNonNull(pool)) {
				throw new IllegalArgumentException(
						"Protocol parsing went wrong. Message does not contain required pools");
			}
		}

		String receivedMessagetype = messageNode.path("messageType").asText();
		if (!Arrays.stream(messageTypes).anyMatch(receivedMessagetype::equals)) {
			throw new IllegalArgumentException(
					"Protocol parsing went wrong. Message does not contain required messageType");

		}

		parsedMessage.put(PictionaryProtocolPool.SENDER, messageNode.path("sender").asText());
		parsedMessage.put(PictionaryProtocolPool.RECEIVER, messageNode.path("receiver").asText());
		parsedMessage.put(PictionaryProtocolPool.MESSAGETYPE, messageNode.path("messageType").asText());
		parsedMessage.put(PictionaryProtocolPool.MESSAGE, messageNode.path("message").asText());

		return parsedMessage;

	}

	/**
	 * Creates the protocol message.
	 *
	 * @param sender the sender
	 * @param receiver the receiver
	 * @param messageType the message type
	 * @param message the message
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 * @throws PictionaryException the pictionary exception
	 */
	public static String createProtocolMessage(String sender, String receiver, String messageType, String message) throws JsonProcessingException, PictionaryException {

		if (!Arrays.stream(messageTypes).anyMatch(messageType::equals)) {
			throw new PictionaryException("Wrong message type.");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode();
		((ObjectNode) node).put("sender", sender);
		((ObjectNode) node).put("receiver", receiver);
		((ObjectNode) node).put("messageType", messageType);
		((ObjectNode) node).put("message", message);
		String protocolMessage = mapper.writeValueAsString(node);

		return protocolMessage;
	}

}
