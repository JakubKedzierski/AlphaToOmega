package server_side.pictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
/**
 * Aggregation class that represents pictionary player
 *
 */
public class PictionaryPlayer implements Comparable<PictionaryPlayer> {

	@Getter
	/** username */
	private String name = null;

	@Getter
	@Setter
	/** The type of player : host/listener */
	private String typeOfPlayer = null;

	@Getter
	@Setter
	/** check if good guess has been already done by user. */
	private boolean goodGuessAlreadyDone = false; 

	@Getter
	/** The points. */
	private int points = 0;

	/**
	 * Instantiates a new pictionary player.
	 *
	 * @param name the name
	 */
	public PictionaryPlayer(String name) {
		this.name = name;
	}

	/**
	 * Instantiates a new pictionary player.
	 *
	 * @param name         the name
	 * @param typeOfPlayer the type of player
	 */
	public PictionaryPlayer(String name, String typeOfPlayer) {
		this.name = name;
		this.typeOfPlayer = typeOfPlayer;
	}

	/**
	 * Adds the points.
	 *
	 * @param points the points
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * Subtract points.
	 *
	 * @param points the points
	 */
	public void subPoints(int points) {
		this.points -= points;
	}

	/**
	 * Compare method based on players points
	 *
	 * @param player the player
	 * @return the int: 1 more points, -1 less points, 0 equally
	 */
	@Override
	public int compareTo(PictionaryPlayer player) {
		if (points > player.getPoints()) {
			return 1;
		} else if (points < player.getPoints()) {
			return -1;
		}
		return 0;
	}

}
