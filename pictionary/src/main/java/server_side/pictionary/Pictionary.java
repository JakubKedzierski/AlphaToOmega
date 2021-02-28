package server_side.pictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import server_side.GameCommunication;
import server_side.PictionaryException;

// TODO: Auto-generated Javadoc
/**
 * The Class Pictionary.
 */
public class Pictionary implements PictionaryInterface {
	
	/** The number of players. */
	private 
 /**
  * Sets the number of players.
  *
  * @param NUMBER_OF_PLAYERS the new number of players
  */
 @Setter int NUMBER_OF_PLAYERS = 2;
	
	/** The number of rounds. */
	private 
 /**
  * Sets the number of rounds.
  *
  * @param NUMBER_OF_ROUNDS the new number of rounds
  */
 @Setter int NUMBER_OF_ROUNDS = 2;
	
	/** The round time. */
	private 
 /**
  * Sets the round time.
  *
  * @param ROUND_TIME the new round time
  */
 @Setter int ROUND_TIME=60*1000;

	/** The word database. */
	private /**
  * Sets the word database.
  *
  * @param wordDatabase the new word database
  */
 @Setter String[] wordDatabase = { "rabbit", "house", "river", "shrek" };
	
	/** The users. */
	private List<PictionaryPlayer> users;
	
	/** The server. */
	private GameCommunication server;
	
	/** The round count. */
	private 
 /**
  * Gets the round count.
  *
  * @return the round count
  */
 @Getter int roundCount;
	
	/** The round. */
	private 
 /**
  * Gets the round.
  *
  * @return the round
  */
 @Getter PictionaryRound round = null;
	
	/** The game running. */
	private 
 /**
  * Checks if is game running.
  *
  * @return true, if is game running
  */
 @Getter boolean gameRunning = false;


	/**
	 * Instantiates a new pictionary.
	 *
	 * @param server the server
	 * @param numberOfPlayers the number of players
	 */
	public Pictionary(GameCommunication server, int numberOfPlayers) {
		this.server = server;
		this.NUMBER_OF_PLAYERS = numberOfPlayers;
		this.users = new ArrayList<PictionaryPlayer>();
		roundCount = 0;
	}

	/**
	 * Adds the user.
	 *
	 * @param name the name
	 */
	public void addUser(String name) {
		if (users.size() < NUMBER_OF_PLAYERS) {
			PictionaryPlayer player = new PictionaryPlayer(name);
			users.add(player);
		} else {
			throw new IllegalArgumentException("MAX users in game already");
		}
	}

	/**
	 * Delete user.
	 *
	 * @param name the name
	 */
	public void deleteUser(String name) {
		for (PictionaryPlayer user : users) {
			if (user.getName().equals(name)) {
				users.remove(user);
			}
		}
	}

	/**
	 * Gets the winner.
	 *
	 * @return the winner
	 */
	public PictionaryPlayer getWinner() {
		PictionaryPlayer winner = Collections.max(users);
		return winner;
	}

	/**
	 * Start game.
	 */
	public void startGame() {
		if (users.size() == NUMBER_OF_PLAYERS && server != null) {
			gameRunning = true;
			gameLoop();
		} else {
			throw new IllegalArgumentException("More users needed to start a game");
		}
	}

	/**
	 * Game loop.
	 */
	private void gameLoop() {
		chooseNewHost();
		round = new PictionaryRound(ROUND_TIME,wordDatabase[roundCount], this);
		roundCount++;
	}

	/**
	 * Choose new host.
	 */
	private void chooseNewHost() {
		for (int i = 0; i < users.size(); i++) {
			PictionaryPlayer player = users.get(i);

			if ((i % NUMBER_OF_PLAYERS) == roundCount) {
				player.setTypeOfPlayer("host");

				sendGameStatusInfo(player.getName(), "host");
				sendGameStatusInfo(player.getName(), "round:" + (roundCount + 1));
				sendGameStatusInfo(player.getName(), "word:" + wordDatabase[roundCount]);
				sendGameStatusInfo(player.getName(), "points:" + player.getPoints());

			} else {

				player.setTypeOfPlayer("listener");
				sendGameStatusInfo(player.getName(), "listener");
				sendGameStatusInfo(player.getName(), "round:" + (roundCount + 1));
				sendGameStatusInfo(player.getName(), "points:" + player.getPoints());

			}
		}

	}

	/**
	 * Check word.
	 *
	 * @param word the word
	 * @param name the name
	 * @return true, if successful
	 */
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

			sendGameStatusInfo(player.getName(), "goodGuess");
			sendGameStatusInfo(player.getName(), "points:" + player.getPoints());

			return true;
		}
		return false;
	}

	/**
	 * Round ended.
	 */
	public void roundEnded() {
		PictionaryPlayer host = getHost();
		host.addPoints(round.getGoodGuessCount());

		for (PictionaryPlayer player : users) {
			player.setGoodGuessAlreadyDone(false);
			sendGameStatusInfo(player.getName(), "round ended");
		}

		if (roundCount < NUMBER_OF_ROUNDS) {
			gameLoop();

		} else {
			PictionaryPlayer winner = getWinner();

			for (PictionaryPlayer player : users) {
				sendGameStatusInfo(player.getName(), "game ended | winner:" + winner.getName());
			}
			gameRunning = false;
		}
	}

	/**
	 * Clean up and unexpected end game.
	 */
	public void cleanUpAndUnexpectedEndGame() {
		if (round.isRunning())
			round.endRound();
		gameRunning = false; // clean up and end game
	}

	/**
	 * Gets the user by name.
	 *
	 * @param name the name
	 * @return the user by name
	 */
	public PictionaryPlayer getUserByName(String name) {
		for (PictionaryPlayer player : users) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public PictionaryPlayer getHost() {
		for (PictionaryPlayer player : users) {
			if (player.getTypeOfPlayer().equals("host")) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Send game status info.
	 *
	 * @param username the username
	 * @param message the message
	 */
	private void sendGameStatusInfo(String username, String message) {
		if (!gameRunning)
			return;

		try {
			server.sendGameInfo(username, message);
		} catch (PictionaryException | IOException e) {
			cleanUpAndUnexpectedEndGame();
		}
	}

	/**
	 * Send periodic time info.
	 *
	 * @param whichPeriod the which period
	 * @param numberOfPeriods the number of periods
	 */
	@Override
	public void sendPeriodicTimeInfo(int whichPeriod, int numberOfPeriods) {
		for (PictionaryPlayer player : users) {
			sendGameStatusInfo(player.getName(), "period:" + whichPeriod + "/" + numberOfPeriods);
		}
	}

}
