package pictionaryTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pictionary.GameCommunication;
import pictionary.Pictionary;

@RunWith(MockitoJUnitRunner.class)
public class PictionaryGameTest {
	
	@Mock
	private GameCommunication server;
	
	private Pictionary game;
	
	
	@Before
	public void setUp() {
		if(server==null) throw new RuntimeException();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkIllegalUserCountInGame() {
		game = new Pictionary(server);
		game.addUser("test");
		game.startGame();
	}
	
	@Test
	public void checkGameDuration() {
		String database[] = {"testWord"};
		game = new Pictionary(server,database,1,2);
		game.addUser("test1");game.addUser("test2");
		game.startGame();
		assertEquals(true,game.checkWord("testWord", "test1"));
		assertEquals(1,game.getUserByName("test1").getPoints());
		assertEquals(0,game.getUserByName("test2").getPoints());
		assertEquals(true,game.isGameRunning());
		
		/*
		try {
			game.checkWord("testWord", "test1");
			fail(); // should throw error becasue we ask second time for word that we already guessed
		}catch(IllegalArgumentException e) {}
		*/
		
		try {
			Thread.sleep(game.getRound().getRoundTime() + 50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(false,game.getRound().isRunning());
		assertEquals(1,game.getUserByName("test1").getPoints()); 
		assertEquals(1,game.getRoundCount());
		assertEquals(false,game.isGameRunning());
	}
	
}
