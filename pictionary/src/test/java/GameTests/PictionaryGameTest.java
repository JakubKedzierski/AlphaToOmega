package GameTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import server_side.GameCommunication;
import server_side.pictionary.Pictionary;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryGameTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class PictionaryGameTest {
	
	/** The server. */
	@Mock
	private GameCommunication server;
	
	/** The game. */
	private Pictionary game;
	
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		if(server==null) throw new RuntimeException();
	}
	
	/**
	 * Check illegal user count in game.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void checkIllegalUserCountInGame() {
		game = new Pictionary(server,4);
		game.addUser("test");
		game.startGame();
	}
	
	/**
	 * Adding too much players.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void addingTooMuchPlayers() {
		game = new Pictionary(server,2);
		game.setNUMBER_OF_ROUNDS(1);
		game.addUser("test1");
		game.addUser("test2");
		game.addUser("test3");
	}
	
	/**
	 * Check game duration.
	 */
	@Test
	public void checkGameDuration() {
		String database[] = {"testWord"};
		game = new Pictionary(server,2);
		game.setNUMBER_OF_ROUNDS(1);
		game.setROUND_TIME(1*1000);
		game.setWordDatabase(database);
		
		
		game.addUser("test1");game.addUser("test2");

		game.startGame();
		assertEquals(true,game.checkWord("testWord", "test2"));
		assertEquals(2,game.getUserByName("test2").getPoints());
		assertEquals(0,game.getUserByName("test1").getPoints());
		assertEquals(true,game.isGameRunning());
		
		try {
			game.checkWord("testWord", "test1");
			fail(); // host cant ask for word guess
		}catch(IllegalArgumentException e) {}
		
		try {
			game.checkWord("testWord", "test2");
			fail(); // should throw error because we ask second time for word that we already guessed
		}catch(IllegalArgumentException e) {}
		
		
		try {
			Thread.sleep(game.getRound().getRoundTime() + 200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(false,game.getRound().isRunning());
		assertEquals(1,game.getUserByName("test1").getPoints()); 
		assertEquals(2,game.getUserByName("test2").getPoints()); 
		assertEquals(1,game.getRoundCount());
		assertEquals(false,game.isGameRunning());
	}
	
}
