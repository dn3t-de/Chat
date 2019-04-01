package client.controller;

import client.controller.chatClient.ChatClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Controller der fxml Datei, die die grafische Oberflaeche enthaelt.
 * In diesem Controller werden alle Funktionen der GUI in Methoden umgewandelt.
 */
public class ClientGUIController implements IClientGUIControllerCallback, Initializable {

    // ChatClient, der die Verbindung zum Server realisiert
    private static ChatClient newClient;
    // Buttons, die auf der GUI dargestellt sind.
    public Button btnVerbindungHerstellen;
    public Button btnSendeNachricht;
    public Button btnVerbindungTrennen;
    public Button btnAnmelden;
    public Button btnAbmelden;
    public Button btnErstelleRaum;
    public Button btnBetrittRaum;
    public Button btnLoescheRaum;
    public Button btnBeenden;
    // Labels, die zur Laufzeot geaendert werden koennen.
    public Label lblAktuellerChannel;
    public Label lblUsername;
    // CheckBox, die benoetigt wird.
    public CheckBox cbROOTKnoten;
    // TextFields und PasswortField, die benoetigt werden.
    public TextField tfServerAdresse;
    public TextField tfNachricht;
    public TextField tfBenutzername;
    public TextField tfNeuerChannelName;
    public PasswordField pfPasswort;
    // TextArea zur Darstellung von Nachrichten
    public TextArea taNachrichten;
    // ListViews zum Anzeigen der Channels und der Nutzer
    public ListView<String> lvChannels;
    public ListView<String> lvNutzer;
    // ArrayList und ObservableList anlegen um Channelnamen zu speichern
    private ArrayList<String> alChannels;
    private ObservableList<String> olChannels;
    // ArrayList und ObservableList anlegen um Nutzernamen zu speichern
    private ArrayList<String> alNutzer;
    private ObservableList<String> olNutzer;

    /**
     * GETTER fuer den ChatClient.
     *
     * @return aktuellen ChatClient
     */
    public static ChatClient getNewClient() {
        return newClient;
    }

    /**
     * SETTER der den ChatClient auf null setzt.
     */
    public static void setNewClientNull() {
        newClient = null;
    }

    /**
     * Wird beim Start der GUI aufgerufen und legt KeyListener bei relevanten Objekten fest.
     *
     * @param url            In diesem Kontext nicht benoetigt.
     * @param resourceBundle In diesem Kontext nicht benoetigt.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Listener registrieren, damit bei "Enter" im tfNachricht die Nachricht gesendet wird.
        tfNachricht.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendeNachricht();
            }
        });

        // Listener registrieren, damit bei "Enter" im pfPasswort die Anmeldung gesendet wird.
        pfPasswort.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                authentifiziereAufServer();
            }
        });

        // Listener registrieren, damit bei "Enter" im tfNeuerChannelName ein neuer Channel erstellt wird.
        tfNeuerChannelName.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                erstelleRaum();
            }
        });
    }

    /**
     * Wird aufgerufen, wenn auf Button "Verbinden" geklickt wird.
     * Erstellt einen neuen Client und leert Nutzer- und Channelliste.
     * Der neue Client erhaelt die IP aus dem TextField "Adresse" und wird gestartet.
     */
    public void verbindeZuServer() {
        // initialisiere ListViews
        initilisiereChannelList();
        initilisiereNutzerList();

        schreibeText("Verbindung herstellen...");
        // Client nur erstellen, wenn eine Adresse eingegeben wurde.
        if (!tfServerAdresse.getText().trim().equals("")) {
            newClient = new ChatClient(tfServerAdresse.getText().trim(), this);
            if (aktiverClient()) {
                // Anmeldebutton aktivieren
                Platform.runLater(() -> {
                    tfBenutzername.setDisable(false);
                    pfPasswort.setDisable(false);
                    btnAnmelden.setDisable(false);
                    btnVerbindungHerstellen.setDisable((true));
                    btnVerbindungTrennen.setDisable(false);
                });
            }
        } else {
            schreibeText("Verbinung konnte nicht hergestellt werden!");
        }

    }

    /**
     * Wird aufgerufen, wenn auf Button "Trennen" geklickt wird.
     * Laesst den Client die Verbindung trennen und leert die Nutzer- und Channelliste.
     */
    public void trenneVerbindung() {
        if (aktiverClient()) {
            newClient.trenneVerbindung();
            setNewClientNull();
            // Listen leeren und Anmelden Button deaktivieren
            Platform.runLater(() -> {
                lblAktuellerChannel.setText("Nicht eingeloggt");
                lblUsername.setText("Nicht eingeloggt");
                lvChannels.setItems(null);
                lvNutzer.setItems(null);
                tfBenutzername.setDisable(true);
                pfPasswort.setDisable(true);
                btnAnmelden.setDisable(true);
                btnAbmelden.setDisable(true);
                btnVerbindungHerstellen.setDisable((false));
                btnVerbindungTrennen.setDisable(true);
            });
        }
    }

    /**
     * Wird aufgerufen, wenn auf Button "Beenden" geklickt wird.
     * Trennt die Verbindung zum Server und beendet den Client.
     */
    public void beendeClient() {
        this.trenneVerbindung();
        Stage stage = (Stage) btnBeenden.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    /**
     * Ertellt eine leere Benutzerliste und uebergibt sie an die ListView der Benutzer.
     */
    private void initilisiereNutzerList() {
        alNutzer = new ArrayList<>();
        olNutzer = FXCollections.observableArrayList(alNutzer);

        Platform.runLater(() -> lvNutzer.setItems(olNutzer));
    }

    /**
     * Erstellt eine leere Channelliste und uebergibt sie an die ListView der Channels.
     */
    private void initilisiereChannelList() {
        alChannels = new ArrayList<>();
        olChannels = FXCollections.observableArrayList(alChannels);

        Platform.runLater(() -> lvChannels.setItems(olChannels));
    }

    /**
     * Wird aufgerufen, wenn auf Button "Anmelden" geklickt wird.
     * uebergibt Benutzername und Passwort an den Client, damit dieser sich beim Server anmelden kann.
     */
    public void authentifiziereAufServer() {
        if (aktiverClient()) {
            // Falls Benutzername oder Passwort nicht eingegeben wurde, wird nichts gemacht.
            if (!(tfBenutzername.getText().equals("") || pfPasswort.getText().equals(""))) {
                Platform.runLater(() -> {
                    tfBenutzername.setText(tfBenutzername.getText().toLowerCase());
                    newClient.einloggen(tfBenutzername.getText(), pfPasswort.getText());
                    lblUsername.setText(tfBenutzername.getText());
                    tfBenutzername.setDisable(true);
                    pfPasswort.setDisable(true);
                    btnAnmelden.setDisable(true);
                    btnAbmelden.setDisable(false);
                });
            } else {
                this.schreibeText("Bitte gib einen Benutzernamen und ein Passwort ein!");
            }
        }
    }

    /**
     * Loggt den Client aus dem Server aus.
     */
    public void ausloggen() {
        if (aktiverClient()) {
            newClient.ausloggen();
        }
    }

    /**
     * Wird aufgerufen, wenn auf Button "Senden" geklickt wird.
     * uebergibt die Nachricht an den Client, der sie an den Server schicken soll.
     */
    public void sendeNachricht() {
        if (aktiverClient()) {
            String nachricht = tfNachricht.getText().trim();
            if (!nachricht.equals("")) {
                newClient.sendeNachricht(nachricht);
            } else {
                this.schreibeText("Bitte gib eine Nachricht ein!");
            }

            Platform.runLater(() -> tfNachricht.setText(""));
        }
    }

    /**
     * Wird aufgerufen, wenn auf Button "Raum betreten" geklickt wird.
     * uebergibt den Channelnamen an den Client, in den der Benutzer wechseln will.
     */
    public void betrittRaum() {
        if (aktiverClient()) {
            if (lvChannels.getSelectionModel().getSelectedItem() != null && !lvChannels.getSelectionModel().getSelectedItem().equals("")) {
                newClient.betreteRaum(lvChannels.getSelectionModel().getSelectedItem());
            } else {
                this.schreibeText("Bitte waehle einen Channel aus!");
            }
        }
    }

    /**
     * Wird aufgerufen, wenn auf Button "Raum loeschen" geklickt wird.
     * uebergibt den Channelnamen an den Client, der geloescht werden soll.
     */
    public void loescheRaum() {
        if (aktiverClient()) {
            if (lvChannels.getSelectionModel().getSelectedItem() != null && !lvChannels.getSelectionModel().getSelectedItem().equals("")) {
                newClient.loescheChannel(lvChannels.getSelectionModel().getSelectedItem());
            } else {
                this.schreibeText("Bitte waehle einen Channel aus!");
            }
        }
    }

    /**
     * Wird aufgerufen, wenn auf Button "Raum erstellen" geklickt wird.
     * uebergibt den eingegebenen Channelnamen und den Namen des Elternchannels an den Client.
     * Der Client soll den Channel am Server erstellen.
     */
    public void erstelleRaum() {
        if (aktiverClient()) {
            String elternChannel = "";
            if (cbROOTKnoten.isSelected()) {
                elternChannel = "ROOT";
            } else if (lvChannels.getSelectionModel().getSelectedItem() != null) {
                elternChannel = lvChannels.getSelectionModel().getSelectedItem();
            }
            if (!tfNeuerChannelName.getText().equals("") && !elternChannel.equals("")) {
                newClient.erstelleChannel(tfNeuerChannelName.getText(), elternChannel);
            } else {
                this.schreibeText("Bitte gib einen Channelnamen ein und waehle einen Elternchannel!");
            }
        }
    }

    /**
     * Prueft, ob es einen aktiven Client gibt, der eine Verbindung zu einem Server hat.
     * PROBLEM: Die ZA Klasse Client gibt bei "istVerbunden()" immer dann true zurueck, wenn der Client erstellt wurde.
     *
     * @return true, falls es einen Client gibt, der eine Verbindung hat, sonst false.
     */
    private boolean aktiverClient() {
        if (newClient != null && newClient.istVerbunden()) {
            return true;
        }
        return false;
    }

    @Override
    public void schreibeText(String text) {
        Platform.runLater(() -> taNachrichten.appendText(text + "\n"));
    }

    @Override
    public void setzeNeuenChannel(String channelName) {
        Platform.runLater(() -> lblAktuellerChannel.setText(channelName));
    }

    @Override
    public void channelHinzufuegen(String channelName, String elternChannel) {
        // Auf unterster Stufe hinzufuegen, falls die Eltern "ROOT" sind.
        if (elternChannel.equals("ROOT")) {
            alChannels.add(channelName);
        } else {
            // neue ArrayList erstellen und die alte Arraylist einfuegen.
            ArrayList<String> newChannelAL = new ArrayList<>();
            for (String channel : alChannels) {
                newChannelAL.add(channel);

                // Falls der neue Channel den gerade eingefuegten als Eltern hat, wird der neue Channel hinter diesem eingefuegt.
                if (channel.trim().equals(elternChannel.trim())) {
                    String tabZaehler = channel;
                    while (tabZaehler.startsWith("\t")) {
                        tabZaehler = tabZaehler.replace("\t", "");
                        channelName = "\t" + channelName;
                    }
                    newChannelAL.add("\t" + channelName);
                }

            }
            // neue Channelliste als die alte
            alChannels = newChannelAL;
        }
        // ArrayList in OberservableArrayList umwandeln und als Items der ListView der Channels uebernehmen.
        olChannels = FXCollections.observableArrayList(alChannels);
        Platform.runLater(() -> lvChannels.setItems(olChannels));

    }

    @Override
    public void channelLoeschen(String channelName) {
        // durchlaufe alle Channels.
        for (String channel : alChannels) {
            // Falls der richtige gefunden wurde, loesche ihn und brich die Schleife ab.
            if (channel.trim().equals(channelName)) {
                alChannels.remove(alChannels.indexOf(channel));
                break;
            }
        }
        // ArrayList in OberservableArrayList umwandeln und als Items der ListView der Channels uebernehmen.
        olChannels = FXCollections.observableArrayList(alChannels);
        Platform.runLater(() -> lvChannels.setItems(olChannels));
    }

    @Override
    public void benutzerZuListeHinzufuegen(String benutzerName) {
        alNutzer.add(benutzerName);

        // ArrayList in OberservableArrayList umwandeln und als Items der ListView der Benutzer uebernehmen.
        olNutzer = FXCollections.observableArrayList(alNutzer);
        Platform.runLater(() -> lvNutzer.setItems(olNutzer));
    }

    @Override
    public void benutzerAusChannelLoeschen(String benutzerName) {
        alNutzer.remove(benutzerName);

        // ArrayList in OberservableArrayList umwandeln und als Items der ListView der Benutzer uebernehmen.
        olNutzer = FXCollections.observableArrayList(alNutzer);
        Platform.runLater(() -> lvNutzer.setItems(olNutzer));
    }

    @Override
    public void updateUserList(String benutzerListe) {
        alNutzer = new ArrayList<>();
        String[] nutzerNamen = benutzerListe.split(",");
        Collections.addAll(alNutzer, nutzerNamen);

        // ArrayList in OberservableArrayList umwandeln und als Items der ListView der Benutzer uebernehmen.
        olNutzer = FXCollections.observableArrayList(alNutzer);
        Platform.runLater(() -> lvNutzer.setItems(olNutzer));
    }

    @Override
    public void setzeLoginZurueck() {
        Platform.runLater(() -> {
            lblUsername.setText("Nicht eingeloggt!");
            tfBenutzername.setDisable(false);
            pfPasswort.setDisable(false);
            btnAnmelden.setDisable(false);
            btnAbmelden.setDisable(true);
        });
    }

    @Override
    public void ausgeloggt() {
        Platform.runLater(() -> {
            alChannels = new ArrayList<>();
            alNutzer = new ArrayList<>();
            lvChannels.setItems(null);
            lvNutzer.setItems(null);
            lblAktuellerChannel.setText("Nicht eingeloggt");
            lblUsername.setText("Nicht eingeloggt");
            tfBenutzername.setDisable(false);
            pfPasswort.setDisable(false);
            btnAnmelden.setDisable(false);
            btnAbmelden.setDisable(true);
        });

    }
}
