package server_side.pictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class PictionaryPlayer implements Comparable<PictionaryPlayer> {

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	private @Getter String name = null;

	/** The type of player. */
	private
	/**
	 * Gets the type of player.
	 *
	 * @return the type of player
	 */
	@Getter
	/**
	 * Sets the type of player.
	 *
	 * @param typeOfPlayer the new type of player
	 */
	@Setter String typeOfPlayer = null;

	/** The good guess already done. */
	private
	/**
	 * Checks if is good guess already done.
	 *
	 * @return true, if is good guess already done
	 */
	@Getter
	/**
	 * Sets the good guess already done.
	 *
	 * @param goodGuessAlreadyDone the new good guess already done
	 */
	@Setter boolean goodGuessAlreadyDone = false; // to check if user already guess the word

	/** The points. */
	private
	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	@Getter int points = 0;

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
	 * Sub points.
	 *
	 * @param points the points
	 */
	public void subPoints(int points) {
		this.points -= points;
	}

	/**
	 * Compare to.
	 *
	 * @param player the player
	 * @return the int
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
