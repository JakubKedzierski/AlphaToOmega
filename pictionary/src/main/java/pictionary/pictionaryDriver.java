package pictionary;

import java.io.IOException;

import client_side.PictionaryClient;
import client_side.PictionaryClientException;
import server_side.PictionaryServer;

public class pictionaryDriver {

	public static void main(String[] args) {
		new PictionaryServer();
		try {
			Thread.sleep(1000);
			PictionaryClient client1=  new PictionaryClient("test1");
			Thread.sleep(400);
			PictionaryClient client2=new PictionaryClient("test2");
			Thread.sleep(400);
			PictionaryClient client3=new PictionaryClient("test3");
			Thread.sleep(400);
			PictionaryClient client4 = new PictionaryClient("test4");
			Thread.sleep(400);
			client4.disconnect();
			client1.sendMessage("chat", "test message", "broadcast");
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
