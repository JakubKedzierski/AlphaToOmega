package pictionarySimplifiedClassesForTests;

import lombok.Getter;
import server_side.GameCommunication;
import server_side.pictionary.Pictionary;

// TODO: Auto-generated Javadoc
/**
 * The Class PictionaryTestClass.
 */
public class PictionaryTestClass extends Pictionary {
	
	/** The round ended. */
	private /**
  * Checks if is round ended.
  *
  * @return true, if is round ended
  */
 @Getter boolean roundEnded=false;
	
	/**
	 * Instantiates a new pictionary test class.
	 *
	 * @param server the server
	 */
	public PictionaryTestClass(GameCommunication server) {
		super(server,4);
	}
	
	/**
	 * Round ended.
	 */
	@Override
	public void roundEnded() {
		roundEnded=true;
	}
	
	/**
	 * Send periodic time info.
	 *
	 * @param whichPeriod the which period
	 * @param numberOfPeriods the number of periods
	 */
	@Override
	public void sendPeriodicTimeInfo(int whichPeriod, int numberOfPeriods) {
		System.out.println("periodic info sended \n" + "period:" + whichPeriod + "/" + numberOfPeriods);
	}

	
}
