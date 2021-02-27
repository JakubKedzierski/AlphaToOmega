package client_side.gui.view_controller;


import client_side.PictionaryClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;


public class ClientStartUpDialogController {
	
	@FXML
	private TextField usernameField;

	private PictionaryClientApp app;

	@FXML
	private void initialize() {
	}

	public void setMainApp(PictionaryClientApp mainApp) {
		this.app = mainApp;
	}
	
	private void invalidUsername() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(app.getPrimaryStage());
		alert.setTitle("Invalid Username.");
		alert.setHeaderText("Your username is taken or is empty \n" + 
							"Please type in another username");
		alert.showAndWait();
	}

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
			app.waitForOtherPlayers();
		}else {
			invalidUsername();
		}
	}
	public void start() {
		app.startGame();
	}

}
