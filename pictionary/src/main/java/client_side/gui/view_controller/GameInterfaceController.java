package client_side.gui.view_controller;

import java.io.IOException;
import client_side.gui.model.PictionaryClient;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

	private final int maxBrushSize = 18;
	private final int minBrushSize = 3;
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
	private Button clearButton;
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
	@FXML
	private Label userPointsLabel;
	@FXML
	private Label guessingWordInfoLabel;
	@FXML
	private Label guessingWordLabel;
	@FXML
	private Slider brushSizeSlider;
	@FXML
	private ColorPicker brushColorPicker;

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

		userPointsLabel.setText(Integer.toString(0));
		brushSizeSlider.setMax(maxBrushSize);
		brushSizeSlider.setMin(minBrushSize);
		brushSizeSlider.setValue(6);
		brushColorPicker.setValue(Color.BLACK);
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
			guessingWordLabel.setText(guessWordField.getText());

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

		double size = brushSizeSlider.getValue();
		GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
		gc.setFill(brushColorPicker.getValue());

		gc.fillRect(mouse.getX(), mouse.getY(), size, size);

		try {
			client.sendMessage("pixelVector",
					mouse.getX() + ":" + mouse.getY() + "|" + size + "[" + brushColorPicker.getValue(), "broadcast");
		} catch (PictionaryException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void clearEachBoard() {
		cleanBoard();
		try {
			client.sendMessage("pixelVector", "clear", "broadcast");
		} catch (PictionaryException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cleanBoard() {
		Platform.runLater(() -> {
			GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, drawingBoardCanvas.getWidth(), drawingBoardCanvas.getHeight());
		});
	}

	public void goodGuessDone() {
		Platform.runLater(() -> {
			guessWordButton.setDisable(true);
			guessWordField.setDisable(true);
		});
	}

	public void drawImageFromHost(double x, double y, double size, String color) {
		if (typeOfPlayerLabel.getText().equals("host"))
			return;

		GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
		gc.setFill(Color.web(color));
		gc.fillRect(x, y, size, size);
	}

	public void showWordToGuess(String word) {

		Platform.runLater(() -> {
			guessingWordLabel.setText(word);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Host info");
			alert.setHeaderText("You are host! Your word to show is: " + word);
			alert.setContentText("Good luck! Your time has started, you have 1 minute.");
			alert.showAndWait();
		});
	}

	public void endGame(String winner) {
		Platform.runLater(() -> {
			ButtonType endGame = new ButtonType("End game");
			Alert a = new Alert(AlertType.NONE, "Game Over", endGame);
			a.setTitle("Game over");
			a.setHeaderText("Game over");
			a.setResizable(false);
			a.setContentText("You get " + userPointsLabel.getText() + " points\n" + "Winner of the game is: " + winner);
			a.showAndWait().ifPresent(response -> {
				if (response == endGame) {
					client.disconnect();
					Platform.exit();
					System.exit(0);
				}
			});
		});

	}

	public void setHostView() {
		Platform.runLater(() -> {
			guessWordField.setDisable(true);
			guessWordField.setVisible(false);
			guessWordButton.setDisable(true);
			guessWordButton.setVisible(false);
			guessingWordInfoLabel.setText("Your word to present is:");
			guessingWordLabel.setText("");
			typeOfPlayerLabel.setText("host");
			clearButton.setVisible(true);
		});
	}

	public void setListenerView() {
		Platform.runLater(() -> {
			guessWordField.setDisable(false);
			guessWordField.setVisible(true);
			guessWordButton.setDisable(false);
			guessWordButton.setVisible(true);
			guessingWordInfoLabel.setText("Your last guess was:");
			guessingWordLabel.setText("");
			typeOfPlayerLabel.setText("listener");
			clearButton.setVisible(false);
		});
	}

	public void showRound(String round) {
		Platform.runLater(() -> {
			roundLabel.setText(round);
		});
	}

	public void setPoints(int points) {
		Platform.runLater(() -> {
			userPointsLabel.setText(Integer.toString(points));
		});
	}

	public void addMessage(String message) {

		if (message != null) {
			messageConsoleField.appendText(message + "\n");
		}
	}

}
