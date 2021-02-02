package pictionary;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;


public final class pictionaryProtocolParser {
	private static final @Getter String[] protocolPool = { "sender" , "receiver" , "messageType", "message"};
	private static final @Getter String[] messageTypes = { "chat", "pixelVector", "guessedWord", "Error" };
	
	
	public static HashMap<String, String> parseProtocol(String jsonMessage) throws JacksonException {
		HashMap<String, String> parsedMessage=new HashMap<String, String>();
		
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
		
		parsedMessage.put("sender", messageNode.path("sender").asText());
		parsedMessage.put("receiver", messageNode.path("receiver").asText());
		parsedMessage.put("messageType", messageNode.path("messageType").asText());
		parsedMessage.put("message", messageNode.path("message").asText());
		
		return parsedMessage;
		
	}

}
