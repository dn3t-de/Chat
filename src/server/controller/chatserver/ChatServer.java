package server.controller.chatserver;

import common.ListExt.ListExt;
import common.UpdateTyp;
import server.controller.IGUIControllerCallback;
import server.controller.messagehandler.IMessageHandler;
import server.controller.messagehandler.MessageHandler;
import server.data.Benutzerverwaltung;
import server.data.ChannelBaseItem;
import server.data.Channelverwaltung;
import server.data.ClientInfo;

import java.util.logging.Logger;

/**
 * Enthaelt die tatsaechliche Logik des ChatServers, (wie z.B. Verteilen der Nachrichten an die anderen Benutzer im gleichen Channel).
 */
public class ChatServer implements IChatServer, IChatServerCallback {

    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());

    // Default-Channel, der beim Verbinden zum Server automatisch betreten werden soll.
    private static final String DEFAULT_CHANNEL = "LOBBY";

    // GUIController Interface, welches ueber Veraenderungen informiert werden soll, die die Server-GUI betreffen.
    private final IGUIControllerCallback guiController;

    // Interface des MessageHandlers, der Nachrichten an die Clients weiterverarbeiten soll.
    private final IMessageHandler messageHandler;

    /**
     * Konstruktor
     *
     * @param guiController GUIController, der ueber Veraenderungen informiert werden soll, die die Server-GUI betreffen.
     */
    public ChatServer(final IGUIControllerCallback guiController) {
        this.guiController = guiController;
        this.messageHandler = new MessageHandler(this);
    }

    /**
     * Prueft, ob die gegebene Verbindung zu einem bereits authentifizierten Benutzer gehoert.
     *
     * @param client Verbindungsinformation des Clients
     * @return True, wenn der Benutzer bzw. die Verbindung zu einem bereits authentifizierten Benutzer gehoert, sonst false.
     */
    private boolean istAuthentifiziert(ClientInfo client) {
        if (BenutzerVerbindungsMapper.vorhanden(client)) {
            LOGGER.info(client + " (" + BenutzerVerbindungsMapper.getBenutzer(client) + ")" + " ist authentifiziert.");
            return true;
        } else {
            LOGGER.info(client + " ist NICHT authentifiziert.");
            messageHandler.sendeWarnung(client, "Nicht authentifiziert.");
            return false;
        }
    }

    /**
     * Loggt den Benutzer ein, wenn der Benutzername und das Passwort korrekt sind.
     *
     * @param client       Verbindungsinformation des Clients
     * @param benutzerName Benutzername des Clients, der sich einloggen will.
     * @param passwort     Passwort des Benutzers, der sich einloggen will.
     */
    @Override
    public void einloggen(ClientInfo client, String benutzerName, String passwort) {
        benutzerName = bereinigen(benutzerName);
        passwort = bereinigen(passwort);

        LOGGER.info("USER: \"" + benutzerName + "\", PASS: \"" + passwort + "\"");

        if (Benutzerverwaltung.ueberpruefeNutzer(benutzerName, passwort)) {

            if (BenutzerVerbindungsMapper.vorhanden(benutzerName)) {
                ClientInfo alterLogin = BenutzerVerbindungsMapper.getVerbindung(benutzerName);
                this.ausloggen(alterLogin);
            }

            BenutzerVerbindungsMapper.hinzufuegen(benutzerName, client);

            Channelverwaltung.betreteChannel(DEFAULT_CHANNEL, benutzerName);

            // Alle Benutzer im aktuellen channel (DEFAULT_CHANNEL) abfragen.
            ListExt<String> andereBenutzer = Channelverwaltung.werIstSonstImChannel(DEFAULT_CHANNEL, benutzerName);

            ListExt<String> alleBenutzerImChannel = Channelverwaltung.werIstImChannel(DEFAULT_CHANNEL);

            // Verbindungen zu den abgefragten Benutzern holen.
            ListExt<ClientInfo> andereVerbindungen = BenutzerVerbindungsMapper.getVerbindungen(andereBenutzer);

            // Eingeloggtem Client die vorhandenen Channels mitteilen
            ListExt<ChannelBaseItem> channelListe = Channelverwaltung.getListe();
            for (ChannelBaseItem cbi : channelListe.getList()) {
                messageHandler.sendeUpdate(client, UpdateTyp.CHANNEL_ERSTELLEN, cbi.getName() + " " + cbi.getEltern());
            }

            // Teilt dem Client den aktuellen Channelnamen mit
            messageHandler.sendeUpdate(client, UpdateTyp.CHANNEL_NAME, DEFAULT_CHANNEL);

            // Eingeloggtem Client die Liste der aktuellen Channelmitglieder senden
            messageHandler.sendeUpdate(client, UpdateTyp.BENUTZER_LISTE, alleBenutzerImChannel.toString());

            // Allen anderen Clients im Channel mitteilen, dass ein neuer Benutzer den Channel betreten hat
            messageHandler.sendeUpdate(andereVerbindungen, UpdateTyp.BENUTZER_BETRETEN, benutzerName);
            messageHandler.sendeInfo(andereVerbindungen, "Benutzer \"" + benutzerName + "\" hat den Channel betreten.");

            messageHandler.sendeInfo(client, "Erfolgreich eingeloggt.");

            guiController.aktualisiereNutzerListView();
        } else {
            messageHandler.sendeWarnung(client, "Fehler beim Einloggen.");
        }
    }

    /**
     * Loggt den Benutzer aus dem Server aus.
     *
     * @param client Verbindungsinformation des veranlassenden Benutzers
     */
    @Override
    public void ausloggen(ClientInfo client) {
        final String benutzer = BenutzerVerbindungsMapper.getBenutzer(client);
        BenutzerVerbindungsMapper.entfernen(client);

        final String channelName = Channelverwaltung.woIstNutzer(benutzer);
        final ListExt<String> benutzerImChannel = Channelverwaltung.werIstImChannel(channelName);
        final ListExt<ClientInfo> benutzerVerbindungen = BenutzerVerbindungsMapper.getVerbindungen(benutzerImChannel);

        Channelverwaltung.loescheBenutzer(benutzer);
        messageHandler.sendeInfo(client, "Ausgeloggt.");
        messageHandler.sendeUpdate(benutzerVerbindungen, UpdateTyp.BENUTZER_VERLASSEN, benutzer);
        messageHandler.sendeInfo(benutzerVerbindungen, "Benutzer \"" + benutzer + "\" hat den Channel verlassen.");
        guiController.aktualisiereNutzerListView();
    }

    /**
     * Erstellt einen Channel.
     *
     * @param client        Verbindungsinformation des veranlassenden Benutzers
     * @param channelName   Name des zu erstellenden Channels.
     * @param elternChannel uebergeordneter Channel des zu erstellenden Channels.
     */
    @Override
    public void channelErstellen(ClientInfo client, String channelName, String elternChannel) {
        channelName = bereinigen(channelName);
        elternChannel = bereinigen(elternChannel);

        if (istAuthentifiziert(client)) {
            if (Channelverwaltung.channelHinzufuegen(channelName, elternChannel)) {
                ListExt<ClientInfo> verbindungsListe = BenutzerVerbindungsMapper.getVerbindungsListe();
                messageHandler.sendeUpdate(verbindungsListe, UpdateTyp.CHANNEL_ERSTELLEN, channelName + " " + elternChannel);
                guiController.aktualisiereChannelListView();
                messageHandler.sendeInfo(client, "Channel \"" + channelName + "\" wurde erstellt.");
                guiController.aktualisiereChannelListView();
            } else {
                messageHandler.sendeWarnung(client, "Channel \"" + channelName + "\" konnte nicht erstellt werden.");
            }
        }
    }

    /**
     * Loescht den angegebenen Channel.
     *
     * @param client      Verbindungsinformation des veranlassenden Benutzers
     * @param channelName Name des zu loeschenden Channels
     */
    @Override
    public void channelLoeschen(ClientInfo client, String channelName) {
        channelName = bereinigen(channelName);

        if (istAuthentifiziert(client)) {
            if (Channelverwaltung.loescheChannel(channelName)) {
                ListExt<ClientInfo> verbindungsListe = BenutzerVerbindungsMapper.getVerbindungsListe();
                messageHandler.sendeUpdate(verbindungsListe, UpdateTyp.CHANNEL_LOESCHEN, channelName);
                guiController.aktualisiereChannelListView();
                messageHandler.sendeInfo(client, "Channel \"" + channelName + "\" wurde geloescht.");
                guiController.aktualisiereChannelListView();
            } else {
                messageHandler.sendeWarnung(client, "Channel \"" + channelName + "\" konnte nicht geloescht werden.");
            }
        }
    }

    /**
     * Laesst den Client in einen anderen Channel wecheln.
     *
     * @param client      Verbiundungsinformation des zu verschiebenden Benutzers
     * @param channelName Name des Channels in den der Benutzer wechseln moechte.
     */
    @Override
    public void channelWechseln(ClientInfo client, String channelName) {
        channelName = bereinigen(channelName);

        if (istAuthentifiziert(client)) {
            String benutzer = BenutzerVerbindungsMapper.getBenutzer(client);

            // Namen des aktuellen Channels abfragen
            String alterChannel = Channelverwaltung.woIstNutzer(benutzer);

            // Mitglieder des aktuellen Channels abfragen
            ListExt<String> mitgliederAlterChannel = Channelverwaltung.werIstSonstImChannel(alterChannel, benutzer);

            // Verbindungen der Benutzer im aktuellen Channel abfragen
            ListExt<ClientInfo> verbindungenAlterChannel = BenutzerVerbindungsMapper.getVerbindungen(mitgliederAlterChannel);

            // Versuchen in den neuen Channel zu wechseln
            if (Channelverwaltung.betreteChannel(channelName, benutzer)) {

                // Clients des alten Channels ueber Update informieren
                messageHandler.sendeUpdate(verbindungenAlterChannel, UpdateTyp.BENUTZER_VERLASSEN, benutzer);
                messageHandler.sendeInfo(verbindungenAlterChannel, "Benutzer \"" + benutzer + "\" hat den Channel verlassen.");

                // Clients im neuen Channel abfragen
                ListExt<String> mitgliederNeuerChannel = Channelverwaltung.werIstSonstImChannel(channelName, benutzer);

                ListExt<String> alleMitgliederNeuerChannel = Channelverwaltung.werIstImChannel(channelName);

                // Zugehoerige Verbindungen abfragen
                ListExt<ClientInfo> verbindungenNeuerChannel = BenutzerVerbindungsMapper.getVerbindungen(mitgliederNeuerChannel);

                messageHandler.sendeUpdate(client, UpdateTyp.CHANNEL_NAME, channelName);

                // Client des gewechselten Nutzers die Mitgliederliste senden
                messageHandler.sendeUpdate(client, UpdateTyp.BENUTZER_LISTE, alleMitgliederNeuerChannel.toString());

                // Clients informieren
                messageHandler.sendeUpdate(verbindungenNeuerChannel, UpdateTyp.BENUTZER_BETRETEN, benutzer);

                // Info-Nachricht senden
                messageHandler.sendeInfo(verbindungenNeuerChannel, "Benutzer \"" + benutzer + "\" hat den Channel betreten.");

                // Gewechseltem Benutzer separate Info-Nachricht senden
                messageHandler.sendeInfo(client, "Channel \"" + channelName + "\" betreten.");

                // Server-GUI aktualisieren
                guiController.aktualisiereNutzerListView();

            } else {
                messageHandler.sendeWarnung(client, "Channel \"" + channelName + "\" konnte nicht betreten werden.");
            }
        }
    }

    /**
     * Sendet eine Nachricht an alle Nutzer, die sich ebenfalls im Channel befinden.
     *
     * @param client    Verbindungsinformation des Nachricht sendenden Benutzers
     * @param nachricht Inhalt der Nachricht.
     */
    @Override
    public void nachrichtSenden(ClientInfo client, String nachricht) {
        nachricht = bereinigen(nachricht);

        if (istAuthentifiziert(client)) {
            String absender = BenutzerVerbindungsMapper.getBenutzer(client);
            String channelName = Channelverwaltung.woIstNutzer(absender);

            if (channelName != null) {
                ListExt<String> benutzerImChannel = Channelverwaltung.werIstImChannel(channelName);
                ListExt<ClientInfo> empfaenger = BenutzerVerbindungsMapper.getVerbindungen(benutzerImChannel);

                messageHandler.sendeNachricht(empfaenger, absender + ": " + nachricht);
            } else {
                messageHandler.sendeWarnung(client, "Aktueller Channel ungueltig.");
            }
        }
    }

    /**
     * Entfernt Leerzeichen vom Anfang und Ende des uebegebenen Strings.
     * Beispiel:
     * Eingabe: "  Channel X "
     * Rueckgabewert: "Channel X"
     *
     * @param eingabe Zu bereinigender String
     * @return Bereinigter String.
     */
    private String bereinigen(String eingabe) {
        if (eingabe == null) {
            return "";
        } else {
            return eingabe.trim();
        }
    }

    @Override
    public void close() {
        messageHandler.close();
    }
}
