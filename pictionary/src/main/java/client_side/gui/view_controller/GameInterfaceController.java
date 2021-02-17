package client_side.gui.view_controller;

import java.io.IOException;
import client_side.gui.model.PictionaryClient;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.Setter;
import server_side.PictionaryException;

public class GameInterfaceController {

	private PictionaryClient client;

	@FXML
	private TextArea messageConsoleField;
	@FXML
	private TextField messageTypedInField;
	@FXML
	private TextField guessWordField;
	@FXML
	private Button guessWordButton;
	@FXML
	private Label roundLabel;
	@FXML
	private Label typeOfPlayerLabel;
	@FXML
	private Label timeLeftLabel;
	@FXML
	private Label usernameLabel;
	@FXML
	private Canvas drawingBoardCanvas;

	public void setClient(PictionaryClient client) {
		this.client = client;
		usernameLabel.setText(client.getUsername());
	}

	@FXML
	private void initialize() {

		messageTypedInField.textProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

					int location = messageTypedInField.getText().length();
					Platform.runLater(() -> {
						messageTypedInField.positionCaret(location);
					});

				});

		messageConsoleField.textProperty()
				.addListener((ObservableValue<?> observable, Object oldValue, Object newValue) -> {
					messageConsoleField.setScrollTop(Double.MAX_VALUE);
				});

	}

	@FXML
	public void sendMessageOnEnterPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			sendMessage();
		}
	}

	@FXML
	public void sendMessage() {

		if (messageTypedInField.getText() != null || messageTypedInField.getText().length() != 0) {
			try {
				client.sendMessage("chat", messageTypedInField.getText(), "broadcast");
				messageConsoleField.appendText(usernameLabel.getText() + ": " + messageTypedInField.getText() + "\n");
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

	@FXML
	public void drawRectangle(MouseEvent mouse) {
		if (typeOfPlayerLabel.getText().equals("listener"))
			return;

		GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(mouse.getX(), mouse.getY(), 6, 6);
		try {
			client.sendMessage("pixelVector", mouse.getX() + ":" + mouse.getY(), "broadcast");
		} catch (PictionaryException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void goodGuessDone() {
		Platform.runLater(() -> {
			guessWordButton.setDisable(true);
			guessWordField.setDisable(true);
		});
	}

	public void drawImageFromHost(double x, double y) {
		if (typeOfPlayerLabel.getText().equals("host"))
			return;

		GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(x, y, 6, 6);
	}

	public void showWordToGuess(String word) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Host info");
			alert.setHeaderText("You are host! Your word to show is: " + word);
			alert.setContentText("Good luck! Your time has started, you have 1 minute.");
			alert.showAndWait();
		});
	}

	public void endGame() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Game over");
			alert.setHeaderText("Game over");
			alert.showAndWait();
		});
	}

	public void setHostView() {
		Platform.runLater(() -> {
			guessWordField.setDisable(true);
			guessWordField.setVisible(false);
			guessWordButton.setDisable(true);
			guessWordButton.setVisible(false);

			typeOfPlayerLabel.setText("host");
		});
	}

	public void setListenerView() {
		Platform.runLater(() -> {
			guessWordField.setDisable(false);
			guessWordField.setVisible(true);
			guessWordButton.setDisable(false);
			guessWordButton.setVisible(true);
			typeOfPlayerLabel.setText("listener");
		});
	}

	public void showRound(String round) {
		Platform.runLater(() -> {
			roundLabel.setText(round);
		});
	}

	public void addMessage(String message) {

		if (message != null) {
			messageConsoleField.appendText(message + "\n");
		}
	}

}
