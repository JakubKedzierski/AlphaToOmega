package server_side.pictionary;


import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

public class PictionaryRound{
	public static int TIMERS_PERIODS_ON_ONE_ROUND=10;
	private int timerPeriodsCounter=0;
	
	private String wordToGuess;
	private Timer timer;
	private @Getter boolean running = false;
	private @Getter long roundTime = 60 * 1000;
	private @Getter int goodGuessCount = 0;
	private PictionaryInterface pictionary;  // to make callback after round ends


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

	public PictionaryRound(String wordToGuess,PictionaryInterface pictionary) {
		this(60 * 1000, wordToGuess,pictionary);
	}
	

	public void endRound() {
		running = false;
		timer.cancel();timer.purge();
		pictionary.roundEnded();
	}
	

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
