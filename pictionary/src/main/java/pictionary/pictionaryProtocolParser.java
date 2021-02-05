package pictionary;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;


public final class pictionaryProtocolParser {
	private static final String[] protocolPool = { "sender" , "receiver" , "messageType", "message"};
	private static final String[] messageTypes = { "chat", "pixelVector", "guessedWord", "Error", "NameValidation", "gameInfo" };
	
	
	public static HashMap<pictionaryProtocolPool, String> parseProtocol(String jsonMessage) throws JacksonException {
		HashMap<pictionaryProtocolPool, String> parsedMessage=new HashMap<pictionaryProtocolPool, String>();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode messageNode = mapper.readTree(jsonMessage);
		
		for(String pool:protocolPool) {
			if(!messageNode.hasNonNull(pool)) {
				throw new IllegalArgumentException("Protocol parsing went wrong. Message does not contain required pools");
			}
		}
		
		String receivedMessagetype = messageNode.path("messageType").asText();
		if(!Arrays.stream(messageTypes).anyMatch(receivedMessagetype::equals)) {
			throw new IllegalArgumentException("Protocol parsing went wrong. Message does not contain required messageType");
			
		}
		
		parsedMessage.put(pictionaryProtocolPool.SENDER, messageNode.path("sender").asText());
		parsedMessage.put(pictionaryProtocolPool.RECEIVER, messageNode.path("receiver").asText());
		parsedMessage.put(pictionaryProtocolPool.MESSAGETYPE, messageNode.path("messageType").asText());
		parsedMessage.put(pictionaryProtocolPool.MESSAGE, messageNode.path("message").asText());
		
		return parsedMessage;
		
	}

}
