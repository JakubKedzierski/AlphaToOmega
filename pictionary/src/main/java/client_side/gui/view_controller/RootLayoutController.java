package client_side.gui.view_controller;

import client_side.PictionaryClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import lombok.Setter;

/**
 * 
 *  Controller of root layout
 *
 */
public class RootLayoutController {
	@Setter
	private PictionaryClientApp app;
	
	
	@FXML
	private void initialize() {}
	
	@FXML
	private void showAuthorInfo() {
		Alert alert= new Alert(AlertType.INFORMATION);
		alert.setTitle("About game/author");
		alert.setHeaderText("Author: Jakub Kêdzierski \n");
		alert.showAndWait();
	}
	
	@FXML
	private void closeGame() {
		app.stop();
	}
}
