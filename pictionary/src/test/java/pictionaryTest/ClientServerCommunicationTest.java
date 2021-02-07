package pictionaryTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import client_side.PictionaryClient;
import client_side.PictionaryClientException;
import server_side.PictionaryServer;

public class ClientServerCommunicationTest {

	private PictionaryServer server = null;

	@Before
	public void setUp() {
		server = new PictionaryServer();
	}

	@Test
	public void usersListShouldBeEmpty() {
		try {
			Thread.sleep(600);
			PictionaryClient client1 = new PictionaryClient("test1");
			Thread.sleep(300);
			PictionaryClient client2 = new PictionaryClient("test2");
			Thread.sleep(300);
			PictionaryClient client3 = new PictionaryClient("test3");
			Thread.sleep(300);
			PictionaryClient client4 = new PictionaryClient("test4");
			Thread.sleep(300);
			assertEquals(4, server.getUsers().size());
			Thread.sleep(100);
			client1.disconnect();
			client2.disconnect();
			client3.disconnect();
			client4.disconnect();
			Thread.sleep(300);
			
			assertEquals(0, server.getUsers().size());
			assertEquals(0, server.getUsersIdList().size());
			
			/*PictionaryClient client5 =new PictionaryClient("test5");
			Thread.sleep(200);
			client5.disconnect();
			Thread.sleep(300);
			
			assertEquals(0, server.getUsers().size());
			assertEquals(0, server.getUsersIdList().size());*/

		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		} catch (PictionaryClientException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}

	}
	

	@After
	public void cleanUp() {
		server.disconnectServer();
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
