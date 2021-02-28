package GameTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import pictionarySimplifiedClassesForTests.GameCommunicationTestServer;
import pictionarySimplifiedClassesForTests.PictionaryTestClass;
import server_side.pictionary.PictionaryRound;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryRoundTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class PictionaryRoundTest {

	/** The game. */
	PictionaryTestClass game;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		GameCommunicationTestServer test = new GameCommunicationTestServer();
		game=new PictionaryTestClass(test);
		if (game == null)
			throw new RuntimeException();
	}

	/**
	 * Check round duration and word guess.
	 *
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	@Test
	public void checkRoundDurationAndWordGuess() throws IndexOutOfBoundsException
	{
		PictionaryRound round = new PictionaryRound(500, "test", game);
		assertEquals(true, round.isRunning());
		assertEquals(false, round.guessedWord("wrong word"));
		assertEquals(true, round.guessedWord("test"));
		assertEquals(true, round.guessedWord("test"));
		try {
			Thread.sleep(round.getRoundTime() - 200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(false, game.isRoundEnded());
		assertEquals(true, round.isRunning());
		
		try {
			Thread.sleep(round.getRoundTime() + 200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(false, round.isRunning());
		assertEquals(2, round.getGoodGuessCount());
		assertEquals(true, game.isRoundEnded());
		round = new PictionaryRound(500, "test", game);
		assertEquals(true, round.isRunning());
	}

}
