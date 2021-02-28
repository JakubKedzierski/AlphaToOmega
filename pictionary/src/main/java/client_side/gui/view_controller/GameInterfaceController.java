package client_side.gui.view_controller;

import java.io.IOException;

import client_side.gui.model.PictionaryClient;
import javafx.application.Platform;

import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.paint.Color;

import server_side.PictionaryException;

// TODO: Auto-generated Javadoc
/**
 * The Class GameInterfaceController.
 */
public class GameInterfaceController {

	/** The max brush size. */
	private final int maxBrushSize = 18;
	
	/** The min brush size. */
	private final int minBrushSize = 3;
	
	/** The client. */
	private PictionaryClient client;

	/** The message console field. */
	@FXML
	private TextArea messageConsoleField;
	
	/** The message typed in field. */
	@FXML
	private TextField messageTypedInField;
	
	/** The guess word field. */
	@FXML
	private TextField guessWordField;
	
	/** The guess word button. */
	@FXML
	private Button guessWordButton;
	
	/** The clear button. */
	@FXML
	private Button clearButton;
	
	/** The round label. */
	@FXML
	private Label roundLabel;
	
	/** The type of player label. */
	@FXML
	private Label typeOfPlayerLabel;
	
	/** The username label. */
	@FXML
	private Label usernameLabel;
	
	/** The drawing board canvas. */
	@FXML
	private Canvas drawingBoardCanvas;
	
	/** The user points label. */
	@FXML
	private Label userPointsLabel;
	
	/** The guessing word info label. */
	@FXML
	private Label guessingWordInfoLabel;
	
	/** The guessing word label. */
	@FXML
	private Label guessingWordLabel;
	
	/** The brush size slider. */
	@FXML
	private Slider brushSizeSlider;
	
	/** The brush color picker. */
	@FXML
	private ColorPicker brushColorPicker;
	
	/** The progress bar. */
	@FXML
	private ProgressBar progressBar;

	/**
	 * Sets the client.
	 *
	 * @param client the new client
	 */
	public void setClient(PictionaryClient client) {
		this.client = client;
		usernameLabel.setText(client.getUsername());
	}

	/**
	 * Initialize.
	 */
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

	/**
	 * Send message on enter pressed.
	 *
	 * @param event the event
	 */
	@FXML
	public void sendMessageOnEnterPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			sendMessage();
		}
	}

	/**
	 * Send message.
	 */
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

	/**
	 * Guess word.
	 */
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

	/**
	 * Draw rectangle.
	 *
	 * @param mouse the mouse
	 */
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

	/**
	 * Clear each board.
	 */
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

	/**
	 * Sets the progress bar value.
	 *
	 * @param value the value
	 * @param maxValue the max value
	 */
	public void setProgressBarValue(double value, double maxValue) {
		Platform.runLater(() -> {
			double progress = value / maxValue;
			progressBar.setProgress(progress);
		});
	}

	/**
	 * Clean board.
	 */
	public void cleanBoard() {
		Platform.runLater(() -> {
			GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, drawingBoardCanvas.getWidth(), drawingBoardCanvas.getHeight());
		});
	}

	/**
	 * Good guess done.
	 */
	public void goodGuessDone() {
		Platform.runLater(() -> {
			guessWordButton.setDisable(true);
			guessWordField.setDisable(true);
		});
	}

	/**
	 * Draw image from host.
	 *
	 * @param x the x
	 * @param y the y
	 * @param size the size
	 * @param color the color
	 */
	public void drawImageFromHost(double x, double y, double size, String color) {
		if (typeOfPlayerLabel.getText().equals("host"))
			return;

		GraphicsContext gc = drawingBoardCanvas.getGraphicsContext2D();
		gc.setFill(Color.web(color));
		gc.fillRect(x, y, size, size);
	}

	/**
	 * Show word to guess.
	 *
	 * @param word the word
	 */
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

	/**
	 * End game.
	 *
	 * @param winner the winner
	 */
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

	/**
	 * Sets the host view.
	 */
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

	/**
	 * Sets the listener view.
	 */
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

	/**
	 * Show round.
	 *
	 * @param round the round
	 */
	public void showRound(String round) {
		Platform.runLater(() -> {
			roundLabel.setText(round);
		});
	}

	/**
	 * Sets the points.
	 *
	 * @param points the new points
	 */
	public void setPoints(int points) {
		Platform.runLater(() -> {
			userPointsLabel.setText(Integer.toString(points));
		});
	}

	/**
	 * Adds the message.
	 *
	 * @param message the message
	 */
	public void addMessage(String message) {

		if (message != null) {
			messageConsoleField.appendText(message + "\n");
		}
	}

}
