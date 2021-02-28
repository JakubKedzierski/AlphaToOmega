package server_side.pictionary;


import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryRound.
 */
public class PictionaryRound{
	
	/** The timers periods on one round. */
	public static int TIMERS_PERIODS_ON_ONE_ROUND=10;
	
	/** The timer periods counter. */
	private int timerPeriodsCounter=0;
	
	/** The word to guess. */
	private String wordToGuess;
	
	/** The timer. */
	private Timer timer;
	
	/** The running. */
	private 
 /**
  * Checks if is running.
  *
  * @return true, if is running
  */
 @Getter boolean running = false;
	
	/** The round time. */
	private 
 /**
  * Gets the round time.
  *
  * @return the round time
  */
 @Getter long roundTime = 60 * 1000;
	
	/** The good guess count. */
	private 
 /**
  * Gets the good guess count.
  *
  * @return the good guess count
  */
 @Getter int goodGuessCount = 0;
	
	/** The pictionary. */
	private PictionaryInterface pictionary;  // to make callback after round ends


	/**
	 * Instantiates a new pictionary round.
	 *
	 * @param roundTime the round time
	 * @param wordToGuess the word to guess
	 * @param pictionary the pictionary
	 */
	public PictionaryRound(long roundTime, String wordToGuess, PictionaryInterface pictionary) {
		this.roundTime = roundTime;
		this.wordToGuess = wordToGuess;
		this.pictionary=pictionary;
		
		running = true;
	    TimerTask task = new TimerTask() {
	    	public void run() {
	    		timerPeriodsCounter++;
	    		pictionary.sendPeriodicTimeInfo(timerPeriodsCounter, TIMERS_PERIODS_ON_ONE_ROUND);
	    		
		    	if(timerPeriodsCounter>=TIMERS_PERIODS_ON_ONE_ROUND)
		    		endRound();
	        }
	    };
		
		timer = new Timer();
		timer.scheduleAtFixedRate(task,0,roundTime/TIMERS_PERIODS_ON_ONE_ROUND);
	}
	

	/**
	 * End round.
	 */
	public void endRound() {
		running = false;
		timer.cancel();timer.purge();
		pictionary.roundEnded();
	}
	

	/**
	 * Guessed word.
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
