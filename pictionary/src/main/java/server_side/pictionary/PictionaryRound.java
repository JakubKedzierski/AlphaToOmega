package server_side.pictionary;


import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

public class PictionaryRound{
	private String wordToGuess;
	private Timer timer;
	private @Getter boolean running = false;
	private @Getter long roundTime = 50 * 1000;
	private @Getter int goodGuessCount = 0;
	private PictionaryInterface pictionary;  // to make callback after round ends


	public PictionaryRound(long roundTime, String wordToGuess, PictionaryInterface pictionary) {
		this.roundTime = roundTime;
		this.wordToGuess = wordToGuess;
		this.pictionary=pictionary;
		
		running = true;
	    TimerTask task = new TimerTask() {
	        public void run() {
	        	endRound();
	        }
	    };
		
		timer = new Timer();
		timer.schedule(task, roundTime);
	}

	public PictionaryRound(String wordToGuess,PictionaryInterface pictionary) {
		this(10 * 1000, wordToGuess,pictionary);
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
