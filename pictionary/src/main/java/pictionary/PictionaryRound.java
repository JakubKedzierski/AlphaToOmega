package pictionary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import lombok.Getter;

public class PictionaryRound {
	private String wordToGuess;
	private Timer timer;
	private @Getter boolean running = false;
	private @Getter long roundTime = 1 * 1000;
	private Pictionary game;
	private @Getter int goodGuessCount = 0;
	private @Getter ArrayList<PictionaryPlayer> usersWhoAlreadyGuessed = new ArrayList<PictionaryPlayer>();
	private @Getter PictionaryPlayer host = null;

	public PictionaryRound(long roundTime, String wordToGuess, Pictionary game) {
		this.roundTime = roundTime;
		this.wordToGuess = wordToGuess;
		this.game = game;
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

	public PictionaryRound(String wordToGuess, Pictionary game, PictionaryPlayer host) {
		this(1 * 1000, wordToGuess, game);
		this.host=host;
	}
	
	public void startRound() {
		if(!timer.isRunning()) timer.start();
	}

	public void endRound() {
		running = false;
		game.roundEnded();
		System.out.println("Round ends");
	}
	
	public boolean guessedWord(String word,PictionaryPlayer player) {
		if(usersWhoAlreadyGuessed.contains(player)) throw new IllegalArgumentException("This user already guessed the word");
		
		if(guessedWord(word)) {
			usersWhoAlreadyGuessed.add(player);
			return true;
		}
		return false;
	}

	public boolean guessedWord(String word) {
		if (running) {
			if (word.equals(wordToGuess)) {
				goodGuessCount++; // add sending info which guess it is in return tuple (pair<integer, integer>)
				return true;
			}
		} else {
			throw new IllegalArgumentException("Round is stopped");
		}

		return false;
	}

}
