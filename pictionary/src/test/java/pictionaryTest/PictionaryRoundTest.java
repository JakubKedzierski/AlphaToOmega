package pictionaryTest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pictionary.Pictionary;
import pictionary.PictionaryRound;

@RunWith(MockitoJUnitRunner.class)
public class PictionaryRoundTest {

	@Mock
	Pictionary game;

	@Before
	public void setUp() {
		if (game == null)
			throw new RuntimeException();
	}

	@Test
	public void checkRoundDurationAndWordGuess() throws IndexOutOfBoundsException
	{
		PictionaryRound round = new PictionaryRound(400, "test", game);
		assertEquals(true, round.isRunning());
		assertEquals(false, round.guessedWord("wrong word"));
		assertEquals(true, round.guessedWord("test"));
		try {
			Thread.sleep(round.getRoundTime() + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(false, round.isRunning());
	}

}
