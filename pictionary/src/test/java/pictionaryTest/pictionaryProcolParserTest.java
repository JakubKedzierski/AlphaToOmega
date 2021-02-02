package pictionaryTest;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pictionary.pictionaryProtocolParser;

public class pictionaryProcolParserTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldSayProtocolMessageIsInvalid() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode();
		((ObjectNode) node).put("sender", "test");
		((ObjectNode) node).put("receiver", "test");
		((ObjectNode) node).put("messageType", "test");
		((ObjectNode) node).put("message", "test");
		String checkedMessage;
		try {
			checkedMessage = mapper.writeValueAsString(node);
			pictionaryProtocolParser.parseProtocol(checkedMessage);
		} catch (JacksonException e) {
			e.printStackTrace();
		}

	}

	@Test(expected = JacksonException.class)
	public void shouldSayProtocolMessageCanNotBeParsedByJackson() throws JacksonException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode();
		((ObjectNode) node).put("sender", "test");
		((ObjectNode) node).put("receiver", "test");
		((ObjectNode) node).put("messageType", "chat");
		((ObjectNode) node).put("message", "test");
		String checkedMessage = new String();

		try {
			checkedMessage = mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		checkedMessage = checkedMessage + "testMessage312312";
		Map<String, String> testMap;
		testMap = pictionaryProtocolParser.parseProtocol(checkedMessage);

	}

}
