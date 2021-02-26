package GameTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import protocol_parser.PictionaryProtocolParser;
import protocol_parser.PictionaryProtocolPool;
import server_side.PictionaryException;

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
			PictionaryProtocolParser.parseProtocol(checkedMessage);
		} catch (JsonProcessingException e) {
			fail();
		}

	}

	@Test(expected = JsonProcessingException.class)
	public void shouldSayProtocolMessageCanNotBeParsedByJackson() throws JsonProcessingException {
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
			fail();
		}
		checkedMessage = checkedMessage + "testMessage312312";
		PictionaryProtocolParser.parseProtocol(checkedMessage);

	}
	
	@Test
	public void shouldCreateProperProtocolAndParseItWell() {
		try {
			String message=PictionaryProtocolParser.createProtocolMessage("testSender", "testReceiver", "Error", "testMessage");
			System.out.println(message);
			HashMap<PictionaryProtocolPool, String> map= new HashMap<PictionaryProtocolPool, String>();
			map=PictionaryProtocolParser.parseProtocol(message);
			assertEquals("testMessage", map.get(PictionaryProtocolPool.MESSAGE));
			assertEquals("testSender", map.get(PictionaryProtocolPool.SENDER));
			assertEquals("testReceiver", map.get(PictionaryProtocolPool.RECEIVER));
			assertEquals("Error", map.get(PictionaryProtocolPool.MESSAGETYPE));
		} catch (JsonProcessingException | PictionaryException e) {
			fail();
		} 
	}

}
