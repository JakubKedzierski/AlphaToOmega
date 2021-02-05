package pictionaryTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pictionary.Pictionary;
import pictionary.PictionaryRound;

public class PictionaryGameTest {

	@Test
	public void checkRound() {
		GameCommunicationTestServer server=new GameCommunicationTestServer();
		Pictionary game= new Pictionary(server);
		PictionaryRound round = new PictionaryRound("test",game);
		assertEquals(true, round.isRunning());
		assertEquals(false,round.guessedWord("wrong word"));
		assertEquals(true,round.guessedWord("test"));
		try {
			Thread.sleep(PictionaryRound.ROUND_TIME + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(false, round.isRunning());
	}
}
