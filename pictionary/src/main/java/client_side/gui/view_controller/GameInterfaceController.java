package client_side.gui.view_controller;

import java.io.IOException;
import client_side.gui.model.PictionaryClient;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Setter;
import server_side.PictionaryException;

public class GameInterfaceController {

	private @Setter PictionaryClient client;

	@FXML private TextArea messageConsoleField;	
	@FXML private TextField messageTypedInField;
	@FXML private TextField guessWordField;
	

	@FXML
	private void initialize() {
		Platform.setImplicitExit(false);
		
		messageTypedInField.textProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

					int location = messageTypedInField.getText().length();
					Platform.runLater(() -> {
						messageTypedInField.positionCaret(location);
					});

				});
		
		messageConsoleField.textProperty().addListener(
				(ObservableValue<?> observable, Object oldValue,Object newValue) -> {
		    	messageConsoleField.setScrollTop(Double.MAX_VALUE); 
		    }
		);

	}

	@FXML
	public void sendMessage() {
		if (messageTypedInField.getText() != null || messageTypedInField.getText().length() != 0) {
			try {
				client.sendMessage("chat", messageTypedInField.getText(), "broadcast");
				messageTypedInField.setText("");
			} catch (PictionaryException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	public void guessWord() {
		if (guessWordField.getText() != null || guessWordField.getText().length() != 0) {
			try {
				client.sendMessage("guessedWord", guessWordField.getText(), "server");
				guessWordField.setText("");
			} catch (PictionaryException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public void addMessage(String message) {

		if (message != null) {
			messageConsoleField.appendText(message+"\n");
		}
	}

}
