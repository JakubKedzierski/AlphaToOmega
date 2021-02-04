package pictionary;

import java.io.IOException;

import client_side.PictionaryClient;
import client_side.PictionaryClientException;
import server_side.PictionaryServer;

public class pictionaryDriver {

	public static void main(String[] args) {
		PictionaryServer server=new PictionaryServer();
		try {
			Thread.sleep(600);
			PictionaryClient client1=  new PictionaryClient("test1");
			Thread.sleep(300);
			PictionaryClient client2=new PictionaryClient("test2");
			Thread.sleep(300);
			PictionaryClient client3=new PictionaryClient("test3");
			Thread.sleep(300);
			PictionaryClient client4 = new PictionaryClient("test4");
			Thread.sleep(300);
			client1.sendMessage("chat", "test message", "broadcast");
			client1.sendMessage("chat", "trying direct message", "test3");
			
			
			Thread.sleep(600);
			client1.disconnect();
			client2.disconnect();
			client3.disconnect();
			client4.disconnect();
			Thread.sleep(200);
			System.out.println(server.getUsers());
			server.disconnectServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (PictionaryClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
