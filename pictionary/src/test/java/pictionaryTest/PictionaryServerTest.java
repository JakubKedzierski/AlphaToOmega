package pictionaryTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import pictionary.*;


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
	
}
