package server_side.pictionary;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

/**
 * Represents round during pictionary game.
 * 
 * Main purpose is to take care of round time and inform game when time ends
 */
public class PictionaryRound {

	/** The timers periods on one round. */
	public static int TIMERS_PERIODS_ON_ONE_ROUND = 10;

	/** The timer periods counter. */
	private int timerPeriodsCounter = 0;

	/** The word to guess. */
	private String wordToGuess;

	/** The timer. */
	private Timer timer;

	@Getter
	/** check if round is running or round ended. */
	private boolean running = false;

	@Getter
	/** The round time. */
	private long roundTime = 60 * 1000;

	@Getter
	/** The good guess count. */
	private int goodGuessCount = 0;

	/** The pictionary - used to make few callbacks */
	private PictionaryInterface pictionary; 

	/**
	 * Instantiates a new pictionary round.
	 *
	 * @param roundTime   the round time
	 * @param wordToGuess the word to guess
	 * @param pictionary  the pictionary
	 */
	public PictionaryRound(long roundTime, String wordToGuess, PictionaryInterface pictionary) {
		this.roundTime = roundTime;
		this.wordToGuess = wordToGuess;
		this.pictionary = pictionary;

		running = true;
		TimerTask task = new TimerTask() {
			public void run() {
				timerPeriodsCounter++;
				pictionary.sendPeriodicTimeInfo(timerPeriodsCounter, TIMERS_PERIODS_ON_ONE_ROUND);

				if (timerPeriodsCounter >= TIMERS_PERIODS_ON_ONE_ROUND)
					endRound();
			}
		};

		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, roundTime / TIMERS_PERIODS_ON_ONE_ROUND);
	}

	/**
	 * Ends round and clean stuff
	 */
	public void endRound() {
		running = false;
		timer.cancel();
		timer.purge();
		pictionary.roundEnded();
	}

	/**
	 * check if guessing word is proper
	 *
	 * @param word the word
	 * @return true, if successful
	 */
	public boolean guessedWord(String word) {
		if (running) {
			if (word.equals(wordToGuess)) {
				goodGuessCount++;
				return true;
			}
		} else {
			throw new IllegalArgumentException("Round is stopped");
		}

		return false;
	}

}
