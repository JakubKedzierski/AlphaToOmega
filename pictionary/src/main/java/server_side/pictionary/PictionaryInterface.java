package server_side.pictionary;

// TODO: Auto-generated Javadoc
/**
 * The Interface PictionaryInterface.
 */
public interface PictionaryInterface {
	
	/**
	 * Round ended.
	 */
	public void roundEnded();
	
	/**
	 * Send periodic time info.
	 *
	 * @param whichPeriod the which period
	 * @param numberOfPeriods the number of periods
	 */
	public void sendPeriodicTimeInfo(int whichPeriod,int numberOfPeriods);
}
