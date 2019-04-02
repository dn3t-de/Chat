package client;

import client.controller.ClientGUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * MAIN-Klasse des Clients.
 * Von hier aus wird der Client gestartet.
 */
public class Main extends Application {
    /**
     * Start des Programms.
     *
     * @param args Standardarray fuer die main-Methode.
     */
    public static void main(String[] args) {
        // Aufruf der launch Methode aus Application.
        launch(args);
    }

    /**
     * Laedt die fxml-Datei fuer die grafische Oberflaeche und startet das GUI.
     *
     * @param primaryStage Hauptcontainer fuer die JavaFX GUI.
     * @throws Exception Beim Laden der fxml Datei koennen Fehler aufkommen, die erkannt werden sollen.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ClientGUI.fxml"));
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        // FEstlegen, was passiert, wenn auf "X" am Fenster geklickt wird.
        primaryStage.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            if (ClientGUIController.getNewClient() != null) {
                ClientGUIController.getNewClient().trenneVerbindung();
                ClientGUIController.setNewClientNull();
            }
            primaryStage.close();
            Platform.exit();
        });

        primaryStage.show();
    }
}
