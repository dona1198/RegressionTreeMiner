package mapClient;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Classe Main del software. Carica il file .fxml che contiene l'interfaccia grafica e fa partire il programma.
 * @author Donato Lerario
 *
 */
public class Main extends Application {
	
	@Override
    public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("GuiProgetto.fxml"));
        primaryStage.setTitle("Regression Tree Miner");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
