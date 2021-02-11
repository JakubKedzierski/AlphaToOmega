package server_side;


import client_side.gui.model.PictionaryClient;

public class pictionaryDriver {

	public static void main(String[] args) {
		PictionaryServer server=new PictionaryServer();
		try {
			Thread.sleep(600);
			PictionaryClient client1=  new PictionaryClient("test1",true);
			Thread.sleep(300);
			PictionaryClient client2=new PictionaryClient("test2",true);
			Thread.sleep(300);
			PictionaryClient client3=new PictionaryClient("test3",true);
			Thread.sleep(300);
			PictionaryClient client4 = new PictionaryClient("test4",true);
			Thread.sleep(300);
			client1.sendMessage("chat", "test message", "broadcast");
			client1.sendMessage("chat", "trying direct message", "test3");
			
			client1.sendMessage("chat", "trying direct message", "test6"); // should throw error but is not throwing
			
			
			Thread.sleep(1000);
			client1.disconnect();
			client2.disconnect();
			client3.disconnect();
			client4.disconnect();
			System.out.println(server.getUsers());
			server.disconnectServer();
		} catch (Exception e) {
			e.printStackTrace();} 
		
		
	}

}
