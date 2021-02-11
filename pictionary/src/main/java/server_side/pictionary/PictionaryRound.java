package server_side.pictionary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.Timer;

import lombok.Getter;

public class PictionaryRound{
	private String wordToGuess;
	private Timer timer;
	private @Getter boolean running = false;
	private @Getter long roundTime = 1 * 1000;
	private @Getter int goodGuessCount = 0;
	private PictionaryInterface pictionary;  // to make callback after round ends


	public PictionaryRound(long roundTime, String wordToGuess, PictionaryInterface pictionary) {
		this.roundTime = roundTime;
		this.wordToGuess = wordToGuess;
		this.pictionary=pictionary;
		
		running = true;
		timer = new Timer((int) roundTime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				endRound();
			}
		});
		
		timer.setInitialDelay(100);
		timer.setRepeats(false); 
		startRound();
	}

	public PictionaryRound(String wordToGuess,PictionaryInterface pictionary) {
		this(1 * 1000, wordToGuess,pictionary);
	}
	
	public void startRound() {
		if(!timer.isRunning()) timer.start();
	}

	public void endRound() {
		running = false;
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
