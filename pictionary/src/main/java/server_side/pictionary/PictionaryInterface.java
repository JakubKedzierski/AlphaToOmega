package server_side.pictionary;

/**
 *  Interface for round-game callbacks
 */
public interface PictionaryInterface {
	
	/**
	 * Round ended.
	 */
	public void roundEnded();
	
	/**
	 * Send periodic time info.
	 *
	 * @param whichPeriod period
	 * @param numberOfPeriods the number of periods
	 */
	public void sendPeriodicTimeInfo(int whichPeriod,int numberOfPeriods);
}
