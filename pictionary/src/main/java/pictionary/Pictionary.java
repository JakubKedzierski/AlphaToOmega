package pictionary;

import java.util.ArrayList;

import lombok.Getter;
import server_side.PictionaryServerException;

public class Pictionary {
	private int NUMBER_OF_PLAYERS = 4;
	private int NUMBER_OF_ROUNDS = 4;

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
		PictionaryPlayer player = new PictionaryPlayer(name);
		users.add(player);
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
		PictionaryPlayer host = chooseNewHost();
		round = new PictionaryRound(wordDatabase[roundCount], this, host);
		roundCount++;
	}

	private PictionaryPlayer chooseNewHost() {
		PictionaryPlayer host = users.get(roundCount);

		try {
			server.sendHostInfo(host.getName(), wordDatabase[roundCount]);
		} catch (PictionaryServerException cirticalException) {
			cleanUpAndUnexpectedEndGame();
		}

		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
			if (i != roundCount)
				try {
					server.sendListenerInfo(users.get(i).getName());
				} catch (PictionaryServerException cirticalException) {
					cleanUpAndUnexpectedEndGame();
				}
		}
		return host;
	}

	public boolean checkWord(String word, String name) {
		PictionaryPlayer player = getUserByName(name);
		if (round.getHost() == player)
			throw new IllegalArgumentException("This player is a host.");

		if (round.guessedWord(word, player)) {
			player.addPoints(2);
			return true;
		}
		return false;
	}

	public PictionaryPlayer getUserByName(String name) {
		for (PictionaryPlayer player : users) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public void roundEnded() {
		PictionaryPlayer host = round.getHost();
		host.addPoints(round.getGoodGuessCount());

		if (roundCount < NUMBER_OF_ROUNDS) {
			gameLoop();
		} else {
			gameRunning = false;
			
			try {
				server.sendEndGameInfo();
			} catch (PictionaryServerException cirticalException) {
				cleanUpAndUnexpectedEndGame();
			}
		
		}
	}
	
	public void cleanUpAndUnexpectedEndGame() {
		gameRunning = false; // clean up and end game
		
	}

}
