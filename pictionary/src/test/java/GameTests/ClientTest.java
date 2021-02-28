package GameTests;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import client_side.gui.model.PictionaryClient;
import protocol_parser.PictionaryProtocolParser;
import server_side.PictionaryException;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientTest.
 */
public class ClientTest {

	/** The client. */
	PictionaryClient client = new PictionaryClient();
	
	/**
	 * Check proper parsing.
	 */
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
