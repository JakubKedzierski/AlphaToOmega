package pictionaryTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import client_side.PictionaryClient;
import client_side.PictionaryClientException;
import pictionary.*;
import server_side.PictionaryServer;


public class PictionaryServerTest {
	
	private PictionaryServer server=null;
	
	@Before
	public void setUp() {
		server=new PictionaryServer();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
	}
	
	@Test  
	public void connectionTest() {
		PictionaryClient client=new PictionaryClient("test1");
	}
	
	
	@Test
	public void checkIfMessageIsBroadcastedProperly() {
		PictionaryClient client1=new PictionaryClient("test1");
		PictionaryClient client2=new PictionaryClient("test2");
		PictionaryClient client3=new PictionaryClient("test3");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			client1.sendMessage("chat", "test message", "broadcast");
		} catch (PictionaryClientException e) {
			e.printStackTrace();
		}
	}
}
