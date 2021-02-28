package AppAndGuiTests;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import client_side.PictionaryClientApp;
import client_side.gui.model.PictionaryClient;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

//https://stackoverflow.com/questions/28501307/javafx-toolkit-not-initialized-in-one-test-class-but-not-two-others-where-is
// need testFx framework



public class PictionaryAppTest extends PictionaryClientApp{
	
	@Mock
	private PictionaryClient clientMock;
	
	@Before
	public void setupJavaFX() throws RuntimeException {
	    final CountDownLatch latch = new CountDownLatch(1);
	    SwingUtilities.invokeLater(() -> {
	        new JFXPanel(); 
	        latch.countDown();
	    });

	    try {
	        latch.await();
	    } catch (InterruptedException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	@Override
	public void start(Stage primaryStage){
		setClient(clientMock);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Pictionary");
		this.primaryStage.setResizable(false);
	}
	
	@Test
	public void checkIfErrorInformationDialogOccuresAndAppShutsDown()
	{
		try {
			initRootLayout();
		} catch (Exception e) {
			informUserAboutFatalErrors(e.getMessage());
		}
	}
	
}
