package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.controller.ServerGUIController;

/**
 * MAIN-Klasse des Server.
 * Von hier aus wird der Server gestartet.
 */
public class Main extends Application {
    /**
     * MAIN Methode des Servers.
     *
     * @param args bleibt leer
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Laedt die fxml-Datei fuer die graphische Oberflaeche und startet das GUI.
     *
     * @param primaryStage ist der innere Container des GUI.
     * @throws Exception wird bei jeglichen Fehlern geworfen.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ServerGUI.fxml"));
        primaryStage.setTitle("Chat Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        // Festlegen, was passiert, wenn auf "X" am Fenster geklickt wird.
        primaryStage.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            if (ServerGUIController.getNewChatServer() != null) {
                ServerGUIController.schliesseServer();
            }
            primaryStage.close();
            Platform.exit();
        });

        primaryStage.show();
    }
}
