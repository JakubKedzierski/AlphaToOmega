package pictionary;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

public class PictionaryRound {
	private String wordToGuess;
	private Timer timer;
	private @Getter boolean running=false;
	public static long ROUND_TIME=20*1000;
	private Pictionary game;

	public PictionaryRound(String wordToGuess,Pictionary game) {
		this.wordToGuess = wordToGuess;
		this.game=game;
		timer = new Timer();
		running=true;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				endRound();
			}
		}, ROUND_TIME);
	}

	public void endRound() {
		timer.cancel();
		timer.purge();
		running=false;
		game.roundEnded();
		System.out.println("Round ends");
	}
	
	public boolean guessedWord(String word) {
		if(running) {
			if(word.equals(wordToGuess)) return true;
		}else {
			throw new IllegalArgumentException("Round is stopped");
		}
		
		return false;
	}


}
