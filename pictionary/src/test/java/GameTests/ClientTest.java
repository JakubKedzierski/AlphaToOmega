package GameTests;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import client_side.gui.model.PictionaryClient;
import protocol_parser.PictionaryProtocolParser;
import server_side.PictionaryException;

public class ClientTest {

	PictionaryClient client = new PictionaryClient();
	
	@Test
	public void checkProperParsing() {
		try {
			String mess = PictionaryProtocolParser.createProtocolMessage("test", "test2", "gameInfo","period:8/10");
			client.parseProtocolMessage(mess);
		} catch (JsonProcessingException | PictionaryException e) {
			fail();
			e.printStackTrace();
		}
		
	}
}
