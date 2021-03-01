package client_side;

import client_side.gui.model.PictionaryClient;
import client_side.gui.view_controller.ClientStartUpDialogController;
import client_side.gui.view_controller.GameInterfaceController;
import client_side.gui.view_controller.RootLayoutController;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * Main app and client side user interface. App firstly set connection with
 * server and when error occures immediately shuts down. If everything goes well
 * user is asked for surname, then starts waiting for ohter players to connect.
 * Then game starts, after few rounds game ends, app shuts down.
 */
public class PictionaryClientApp extends Application implements ClientApp {

	@Getter
	@Setter
	/** Reference to client which is used to communicate with game server */
	private PictionaryClient client = null;
	

	@Getter
	/** The primary stage. */
	protected Stage primaryStage;

	/** The root layout which is a base for others app layouts */
	private BorderPane rootLayout;

	/**
	 * Inits the root layout and set it as a scene.
	 * 
	 * @throws Exception throws only when unable to load view resources
	 */
	public void initRootLayout() throws Exception {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			RootLayoutController controller = loader.getController();
			controller.setApp(this);

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Fatal error with loading rootLayout");
		}

	}

	/**
	 * Inits preview dialog and set it as a center of root layout. In preview dialog
	 * user is asked for username.
	 * 
	 * @throws Exception throws only when unable to load view resources
	 */
	public void showPreviewDialog() throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/ClientStartUpDialog.fxml"));
			AnchorPane clientStartUpDialog = (AnchorPane) loader.load();
			rootLayout.setCenter(clientStartUpDialog);

			ClientStartUpDialogController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Fatal error with loading previewDialog");
		}
	}

	/**
	 * Init waiting pane and set it as a center of root layout.
	 * 
	 * @throws Exception throws only when unable to load view resources
	 */
	public void waitForOtherPlayers() throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/WaitingStage.fxml"));
			AnchorPane clientStartUpDialog = (AnchorPane) loader.load();
			rootLayout.setCenter(null);
			rootLayout.setCenter(clientStartUpDialog);
			primaryStage.show();
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Fatal error with loading waiting stage view resource");
		}

	}

	/**
	 * Loading new scene for UI, setting UI controller and starting pictionary game
	 * on client side.
	 */
	@Override
	public void startGame() {

		try {
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(getClass().getResource("gui/view_controller/GameInterface.fxml"));
			AnchorPane gameInterface = (AnchorPane) loader.load();

			GameInterfaceController controller = loader.getController();

			controller.setClient(client);
			client.setGuiController(controller);

			Platform.runLater(() -> {
				rootLayout.setCenter(gameInterface);
			});

		} catch (IOException fatalError) {
			fatalError.printStackTrace();
			informUserAboutFatalErrors(fatalError.getMessage());
		}
	}

	/**
	 * Called at application startup, initializes fields and takes care of game/app processing
	 *
	 * @param primaryStage the primary stage main stage where app will be constructed
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Pictionary");
		this.primaryStage.setResizable(false);

		try {
			Image gameIcon = new Image(ClassLoader.getSystemResourceAsStream("client_side/gui/images/game_icon.png"));
			primaryStage.getIcons().add(gameIcon);
		} catch (Exception e) {
			e.printStackTrace();
			informUserAboutFatalErrors("Troubles with loading game icon");
		}

		client = new PictionaryClient(this);

		try {
			initRootLayout();
		} catch (Exception initError) {
			initError.printStackTrace();
			informUserAboutFatalErrors(initError.getMessage());

		}

		try {
			client.startClientConnection();
		} catch (IOException fatalError) {
			fatalError.printStackTrace();
			informUserAboutFatalErrors("Server connection failed \n" + "Please wait a moment and try connect once again.");
		}

		try {
			showPreviewDialog();
		} catch (Exception fatalError) {
			fatalError.printStackTrace();
			informUserAboutFatalErrors(fatalError.getMessage());
		}
	}

	/**
	 *  Tells user about fatal errors and shuts down app
	 * 
	 * @param errorMessage message that is shown to user
	 */
	public void informUserAboutFatalErrors(String errorMessage) {
		ButtonType endGame = new ButtonType("OK :(");
		Alert fatalAlert = new Alert(AlertType.ERROR, "Fatal error.", endGame);
		fatalAlert.setTitle("Fatal error.");
		fatalAlert.setHeaderText("Application encountered fatal erros and needs to be shut down. \n");
		fatalAlert.setContentText("Error information: " + errorMessage);
		fatalAlert.setResizable(false);

		fatalAlert.showAndWait().ifPresent(response -> {
			if (response == endGame) {
				client.disconnect();
				Platform.exit();
				System.exit(0);
			}
		});

	}

	/**
	 *  Called when app is closing, disconnect from server and immediately app is shut down
	 */
	@Override
	public void stop() {
		client.disconnect();
	}

	/**
	 * Launching main app
	 *
	 * @param args arguments that are forwarded to launch method
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
