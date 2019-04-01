package server.controller;

import common.ListExt.ListExt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.controller.chatserver.ChatServer;
import server.controller.chatserver.IChatServer;
import server.data.Benutzerverwaltung;
import server.data.ChannelBaseItem;
import server.data.Channelverwaltung;
import server.data.XMLParser;

import java.util.ArrayList;

/**
 * Controllerklasse fuer die JavaFX GUI.
 * Realisiert das Interface: IGUIControllerCallback
 */
public class ServerGUIController implements IGUIControllerCallback {

    private static IChatServer newChatServer;
    // GUI Elemente
    public Button btnStartServer;
    public Button btnStoppSterver;
    public Button btnNutzerHinzufuegen;
    public Button btnNutzerLoeschen;
    public Button btnBeenden;
    public TextArea taStatus;
    public TextField tfUsername;
    public TextField tfPasswort;
    public ListView<String> lvChannels;
    public ListView<String> lvNutzerliste;

    /**
     * Gibt das Objekt des ChatServers zurueck.
     *
     * @return aktuellen ChatServer fuer den das GUI angezeigt wird, sonst null
     */
    public static IChatServer getNewChatServer() {
        return newChatServer;
    }

    /**
     * Schliesst den Server in ChatServer Objekt.
     */
    public static void schliesseServer() {
        newChatServer.close();
    }

    /**
     * Startet den Server und stoesst das Einlesen der XML-Dateien an.
     * Bei erfolgreichem Start des Servers erfolgt eine Anpassung des GUI.
     */
    public void starteServer() {
        // Nachricht ausgeben
        Platform.runLater(() -> taStatus.appendText("Server startet... \n"));
        // Channels und Benutzer einlesen
        Benutzerverwaltung.benutzerliste = new ListExt<>();
        Channelverwaltung.erstelleInitialenGraphen();
        if (!XMLParser.channelsEinlesen()) {
            Platform.runLater(() -> {
                taStatus.appendText("Serverstart abgebrochen! Bitte ueberpruefe die Channel xml-Datei und starte den Server dann neu!");
                btnStartServer.setDisable(true);
            });
            return;
        }
        if (!XMLParser.benutzerEinlesen()) {
            Platform.runLater(() -> {
                taStatus.appendText("Serverstart abgebrochen! Bitte ueberpruefe die Benutzer xml-Datei und starte den Server dann neu!");
                btnStartServer.setDisable(true);
            });
            return;
        }

        // Channel- und Nutzerliste laden
        Platform.runLater(() -> taStatus.appendText("Lade Channel- und Nutzerliste... \n"));
        aktualisiereChannelListView();
        aktualisiereNutzerListView();


        // starte Verbindungsmanager

        Platform.runLater(() -> taStatus.appendText("Starte Verbindugsmanager... \n"));
        newChatServer = new ChatServer(this);

        // Nachricht ausgeben und Stopp Button aktivieren
        Platform.runLater(() -> {
            btnStoppSterver.setDisable(false);
            btnStartServer.setDisable(true);
            btnNutzerHinzufuegen.setDisable(false);
            btnNutzerLoeschen.setDisable(false);
            taStatus.appendText("Server gestartet! \n");
        });
    }

    /**
     * Der Server wird gestoppt und die Nutzerliste und Channelstruktur werden in XML-Dateien gespeichert.
     * Bei Erfolg wird das GUI angepasst.
     */
    public void stoppServer() {
        newChatServer.close();
        if (!Benutzerverwaltung.speichereBenutzer()) {
            Platform.runLater(() -> taStatus.appendText("Ein unerwartetes Problem beim Speichern der Benutzerliste ist aufgetreten! Die Benutzerliste wurde nicht gespeichert!"));
        }
        if (!Channelverwaltung.speichereChannels()) {
            Platform.runLater(() -> taStatus.appendText("Ein unerwartetes Problem beim Speichern der Channelstruktur ist aufgetreten! Die Channelstruktur wurde nicht gespeichert!"));
        }
        Platform.runLater(() -> {
            lvNutzerliste.setItems(null);
            lvChannels.setItems(null);
            taStatus.appendText("Nutzer und Channels gespeichert! \n");
            btnNutzerHinzufuegen.setDisable(true);
            btnNutzerLoeschen.setDisable(true);
            btnStoppSterver.setDisable(true);
            btnStartServer.setDisable(false);
            taStatus.appendText("Server gestoppt! \n");
        });
    }

    /**
     * Der Server wird beendet und die Nutzerliste und Channelstrutur werden in XML-Dateien gespeichert.
     * Bei Fehlern wird eine Nachricht ausgegeben.
     */
    public void beendeServer() {
        if (newChatServer != null) {
            schliesseServer();
            if (!Benutzerverwaltung.speichereBenutzer()) {
                Platform.runLater(() -> System.out.println("Ein unerwartetes Problem beim Speichern der Benutzerliste ist aufgetreten! Die Benutzerliste wurde nicht gespeichert!"));
            }
            if (!Channelverwaltung.speichereChannels()) {
                Platform.runLater(() -> System.out.println("Ein unerwartetes Problem beim Speichern der Channelstruktur ist aufgetreten! Die Channelstruktur wurde nicht gespeichert!"));
            }
        }
        Stage stage = (Stage) btnBeenden.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    /**
     * Der angegebene Nutzer wird zu den Benutzern hinzugefuegt.
     */
    public void nutzerHinzufuegen() {
        // Wenn eines der Felder leer ist, so wird nichts gemacht
        if (!tfUsername.getText().equals("") && !tfPasswort.getText().equals("")) {
            // Benutzernamen in Benutzerliste hinzufueuegen
            Benutzerverwaltung.benutzerHinzufuegen(tfUsername.getText(), tfPasswort.getText());
            // Felder leeren

            Platform.runLater(() -> {
                tfUsername.setText("");
                tfPasswort.setText("");
            });
            // aktualisiere ListView
            aktualisiereNutzerListView();
        } else {
            Platform.runLater(() -> taStatus.appendText("Bitte Benutzernamen und Passwort eingeben!"));
        }
    }

    /**
     * Der ausgewaehlte Nutzer wird aus den Benutzern geloescht.
     */
    public void nutzerLoeschen() {
        if (lvNutzerliste.getSelectionModel().getSelectedItem() != null) {
            if (Benutzerverwaltung.loescheNutzer(lvNutzerliste.getSelectionModel().getSelectedItem())) {
                aktualisiereNutzerListView();
            }
        } else {
            Platform.runLater(() -> taStatus.appendText("Bitte einen Benutzer auswaehlen!"));
        }
    }

    @Override
    public void aktualisiereChannelListView() {
        ArrayList<String> channelList = new ArrayList<>();
        java.util.List<ChannelBaseItem> graphAlsListe = Channelverwaltung.getListe().getList();
        String channelName;

        // durchlaufe die Liste, erstelle Tabs, wenn noetig vor den Channelnamen, und fuege sie der Channelliste hinzu
        for (ChannelBaseItem channelItem : graphAlsListe) {
            channelName = channelItem.getName();
            if (!channelName.equals("ROOT")) {
                int abstandZuRoot = Channelverwaltung.distanzZuROOT(channelItem.getName());
                for (int i = 1; i < abstandZuRoot; i++) {
                    channelName = "\t" + channelName;
                }
            }
            channelList.add(channelName);
        }

        // Fuege alle gesammelten Channelnamen in die ListView ein
        ObservableList<String> channelListe = FXCollections.observableArrayList(channelList);

        Platform.runLater(() -> lvChannels.setItems(channelListe));
    }

    @Override
    public void aktualisiereNutzerListView() {
        // ListView leeren

        Platform.runLater(() -> lvNutzerliste.setItems(null));

        // Arraylist anlegen um Usernamen zu speichern
        ArrayList<String> benutzernamen = new ArrayList<>();
        // An den Anfang der Benutzerliste gehen
        Benutzerverwaltung.benutzerliste.toFirst();

        // Benutzerliste durchlaufen
        while (Benutzerverwaltung.benutzerliste.hasAccess()) {
            // Benutzernamen in die temporaere ArrayList eintragen
            String nutzerName = Benutzerverwaltung.benutzerliste.getContent().getName();
            String nutzerChannel = Channelverwaltung.woIstNutzer(nutzerName);
            if (nutzerChannel != null) {
                nutzerName = nutzerName + " (" + nutzerChannel + ")";
            }
            benutzernamen.add(nutzerName);
            // zum naechsten Element der Liste gehen
            Benutzerverwaltung.benutzerliste.next();
        }

        // Fuege alle gesammelten Channelnamen in die ListView ein
        ObservableList<String> nutzerliste = FXCollections.observableArrayList(benutzernamen);

        Platform.runLater(() -> lvNutzerliste.setItems(nutzerliste));
    }
}
