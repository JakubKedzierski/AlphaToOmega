package client_side.gui.view_controller;

import client_side.PictionaryClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;


/**
 *  Starting window controller
 *  
 *  Controller is responsible for controlling first stage window and startup dialog with user
 */
public class ClientStartUpDialogController {
	
	/** The username field. */
	@FXML
	private TextField usernameField;

	/** The app. */
	private PictionaryClientApp app;

	/**
	 * Initialize.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Sets the main app.
	 *
	 * @param mainApp the new main app
	 */
	public void setMainApp(PictionaryClientApp mainApp) {
		this.app = mainApp;
	}
	
	/**
	 *  Alert thrown when username is invalid
	 */
	private void invalidUsername() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(app.getPrimaryStage());
		alert.setTitle("Invalid Username.");
		alert.setHeaderText("Your username is taken or is empty \n" + 
							"Please type in another username");
		alert.showAndWait();
	}

	/**
	 * handling and getting new username from user
	 */
	@FXML
	public void handleNewUsername() {
		if(usernameField.getText() == null || usernameField.getText().length()==0){
			invalidUsername(); return;
		}
		
		if(usernameField.getText().equals("test")) app.startGame();
		
		app.getClient().validateName(usernameField.getText());		
		
		try {
			Thread.sleep(500); // wait until respond from server is sent
		}catch(InterruptedException e) {}
		
		if(app.getClient().isValidUsername()) {
			try {
				app.waitForOtherPlayers();
			} catch (Exception error) {
				app.informUserAboutFatalErrors(error.getMessage());
			}
		}else {
			invalidUsername();
		}
	}
	
	/**
	 * starting app when validation is done
	 * 
	 */
	public void start() {
		app.startGame();
	}

}
