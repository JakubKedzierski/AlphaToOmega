package testsWithClientConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import client_side.gui.model.PictionaryClient;
import server_side.PictionaryServer;

public class ClientServerCommunicationTest {

	private PictionaryServer server = null;

	@Test
	public void checkingServersStartup() {
		try {
			server = new PictionaryServer(4,true);
			Thread.sleep(1000);
			server.disconnectServer();
			Thread.sleep(500);
			
			server = new PictionaryServer(2,true);
			Thread.sleep(1000);
			server.disconnectServer();
			Thread.sleep(500);
			
		} catch (InterruptedException | IllegalArgumentException e ) {
			fail();
			e.printStackTrace();
		}

	}
	
	@Test
	public void usersListShouldBeEmpty() {
		server = new PictionaryServer(4,true);
		
		try {
			Thread.sleep(600);
			PictionaryClient client1 = new PictionaryClient("test1");
			Thread.sleep(100);
			PictionaryClient client2 = new PictionaryClient("test2");
			Thread.sleep(100);
			PictionaryClient client3 = new PictionaryClient("test3");
			Thread.sleep(100);
			PictionaryClient client4 = new PictionaryClient("test4");
			Thread.sleep(100);
			assertEquals(4, server.getUsers().size());
			
			Thread.sleep(3000);
			client1.disconnect();
			client2.disconnect();
			client3.disconnect();
			client4.disconnect();
			Thread.sleep(500);

			assertEquals(0, server.getUsers().size());
			assertEquals(0, server.getUsersIdList().size());
			
			server.disconnectServer();
			Thread.sleep(500);

		} catch (InterruptedException | IOException | IllegalArgumentException e) {
			server.disconnectServer();
			fail();
			return;
		}
		
	}
	
	@Test
	public void shouldThrowExceptionWithListeningThreadCauseOfTwoRunningServers() {
		try {
			server = new PictionaryServer(4,true);
			Thread.sleep(2000);
		} catch (InterruptedException | IllegalArgumentException e ) {
			fail();
			server.disconnectServer();
		}
		new PictionaryServer(2,true);
		server.disconnectServer();
	}
	
	@Test(timeout=5000)
	public void checkingConnectionTimeout() {     // throw timeout error - need to fix in server and client connection
		server = new PictionaryServer(2,true);
		
		try {
			Thread.sleep(500);
			new PictionaryClient("test1");
			Thread.sleep(100);
			new PictionaryClient("test2");
			Thread.sleep(100);

		} catch (InterruptedException| IOException |IllegalArgumentException e) {
			server.disconnectServer();
			fail();
		}
		
		try {
			PictionaryClient client3 = new PictionaryClient();
			client3.startClientConnection();
			Thread.sleep(PictionaryClient.CONNECTION_TIMEOUT+100); 
			fail();
		} catch (IOException | InterruptedException e) {}
		
	}

}
