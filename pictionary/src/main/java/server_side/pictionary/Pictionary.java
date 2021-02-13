package server_side.pictionary;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import server_side.GameCommunication;
import server_side.PictionaryException;

public class Pictionary implements PictionaryInterface {
	private int NUMBER_OF_PLAYERS = 2;
	private int NUMBER_OF_ROUNDS = 2;

	private String[] wordDatabase = { "rabbit", "house", "river", "shrek" };
	private ArrayList<PictionaryPlayer> users;
	private GameCommunication server;
	private @Getter int roundCount;
	private @Getter PictionaryRound round = null;
	private @Getter boolean gameRunning = false;

	public Pictionary(GameCommunication server, String[] wordDatabase, int numberOfRounds, int numberOfPlayers) {
		this(server, wordDatabase);
		this.NUMBER_OF_PLAYERS = numberOfPlayers;
		this.NUMBER_OF_ROUNDS = numberOfRounds;
	}

	public Pictionary(GameCommunication server, String[] wordDatabase) {
		this(server);
		this.wordDatabase = wordDatabase;
	}

	public Pictionary(GameCommunication server) {
		this.server = server;
		this.users = new ArrayList<PictionaryPlayer>();
		roundCount = 0;
	}

	public void addUser(String name) {
		if (users.size() < NUMBER_OF_PLAYERS) {
			PictionaryPlayer player = new PictionaryPlayer(name);
			users.add(player);
		} else {
			throw new IllegalArgumentException("MAX users in game already");
		}
	}

	public void deleteUser(String name) {
		for (PictionaryPlayer user : users) {
			if (user.getName().equals(name)) {
				users.remove(user);
			}
		}
	}

	public void startGame() {
		if (users.size() == NUMBER_OF_PLAYERS && server != null) {
			gameRunning = true;
			gameLoop();
		} else {
			throw new IllegalArgumentException("More users needed to start a game");
		}
	}

	private void gameLoop() {
		chooseNewHost();
		round = new PictionaryRound(wordDatabase[roundCount], this);
		roundCount++;
	}

	private void chooseNewHost() {
		for (int i = 0; i < users.size(); i++) {
			PictionaryPlayer player = users.get(i);

			if (i == roundCount) {
				player.setTypeOfPlayer("host");
				try {
					server.sendGameInfo(player.getName(), "host");
					server.sendGameInfo(player.getName(), "round:"+ (roundCount+1));
					server.sendGameInfo(player.getName(), "word:" + wordDatabase[roundCount]);
				} catch (PictionaryException | IOException cirticalException) {
					cleanUpAndUnexpectedEndGame();
				}
			}

			else {
				player.setTypeOfPlayer("listener");
				try {
					server.sendGameInfo(player.getName(), "listener");
					server.sendGameInfo(player.getName(), "round:"+ (roundCount+1));
				} catch (PictionaryException | IOException cirticalException) {
					cleanUpAndUnexpectedEndGame();
				}
			}
		}

	}

	public boolean checkWord(String word, String name) {
		PictionaryPlayer player = getUserByName(name);

		if (player == null)
			throw new IllegalArgumentException("Player not found");
		if (player.getTypeOfPlayer().equals("host"))
			throw new IllegalArgumentException("Player is host");
		if (player.isGoodGuessAlreadyDone())
			throw new IllegalArgumentException("Player already made a guess");

		if (round.guessedWord(word)) {
			player.addPoints(2);
			player.setGoodGuessAlreadyDone(true);
			return true;
		}
		return false;
	}

	public void roundEnded() {
		PictionaryPlayer host = getHost();
		host.addPoints(round.getGoodGuessCount());

		for (PictionaryPlayer player : users) {
			player.setGoodGuessAlreadyDone(false);
			try {
				server.sendGameInfo(player.getName(), "round ended");
			} catch (PictionaryException | IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		if (roundCount < NUMBER_OF_ROUNDS) {
			gameLoop();
		} else {
			
			gameRunning = false;
			try {
				for (PictionaryPlayer player : users) {
					server.sendGameInfo(player.getName(), "game ended");
				}
			} catch (PictionaryException | IOException cirticalException) {
				cleanUpAndUnexpectedEndGame();
			}

		}
	}

	public void cleanUpAndUnexpectedEndGame() {
		if (round.isRunning())
			round.endRound();
		gameRunning = false; // clean up and end game
	}

	public PictionaryPlayer getUserByName(String name) {
		for (PictionaryPlayer player : users) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public PictionaryPlayer getHost() {
		for (PictionaryPlayer player : users) {
			if (player.getTypeOfPlayer().equals("host")) {
				return player;
			}
		}
		return null;
	}

}
