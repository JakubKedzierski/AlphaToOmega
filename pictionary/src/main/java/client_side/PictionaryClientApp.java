package client_side;

import client_side.gui.model.PictionaryClient;
import client_side.gui.view_controller.ClientStartUpDialogController;
import client_side.gui.view_controller.GameInterfaceController;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;

public class PictionaryClientApp extends Application implements ClientApp {
	private @Getter PictionaryClient client = null;

	private @Getter Stage primaryStage;
	private BorderPane rootLayout;

	public String askForName() {
		return "testName";
	}

	public void initRootLayout() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showPreviewDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/ClientStartUpDialog.fxml"));
			AnchorPane clientStartUpDialog = (AnchorPane) loader.load();
			rootLayout.setCenter(clientStartUpDialog);

			ClientStartUpDialogController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void waitForOtherPlayers() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("gui/view_controller/WaitingStage.fxml"));
			AnchorPane clientStartUpDialog = (AnchorPane) loader.load();
			rootLayout.setCenter(null);
			rootLayout.setCenter(clientStartUpDialog);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void startGame() {
		
		try {
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(getClass().getResource("gui/view_controller/GameInterface.fxml"));
			AnchorPane gameInterface = (AnchorPane) loader.load();

			GameInterfaceController controller = loader.getController();
			
			controller.setClient(client);
			client.setGuiController(controller);
			
			Platform.runLater(()-> {
					rootLayout.setCenter(gameInterface);	
				});
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Pictionary");
		this.primaryStage.setResizable(false);
		
		try {
			Image gameIcon= new Image(ClassLoader.getSystemResourceAsStream("client_side/gui/images/game_icon.png"));
			primaryStage.getIcons().add(gameIcon);
		}catch(Exception e) {
			System.out.println("Trobules with loading game icon");
			e.printStackTrace();
		}
			
		client = new PictionaryClient(this);
		
		initRootLayout();
		try {
			client.startClientConnection();
		} catch (IOException fatalError) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(primaryStage);
			alert.setTitle("Server connection error.");
			alert.setHeaderText("Server connection failed \n" + "Please wait a moment and try connect once again.");
			alert.showAndWait();
			System.exit(-1);
		}
		showPreviewDialog();
	}

	@Override
	public void stop() {
		client.disconnect();
	}

	public static void main(String[] args) {
		launch(args);
	}


}
