package client.controller.chatClient;

import client.controller.IClientGUIControllerCallback;
import client.controller.MessageHandler.IMessageHandler;
import client.controller.MessageHandler.MessageHandler;
import za.netzklassen.Client;

/**
 * ChatClient der die Verbindung mit dem Server haelt und Aktionen vom Server realisiert und an den Server sendet.
 * Erbt von Client aus den ZA Klassen
 * Realisiert Interface: IChatClientCallback
 */
public class ChatClient extends Client implements IChatClientCallback {

    private final IClientGUIControllerCallback clientGUIController;
    private final IMessageHandler messageHandler;

    /**
     * Konstruktor des ChatClients.
     * Startet die super-Klasse ZA.Client und erzeugt eine Instanz des Messagehandlers.
     *
     * @param serverIP               IP des Servers.
     * @param newClientGUIController Instanz der graphischen Oberflaeche, damit nspaeter darauf zugegriffen werden kann.
     */
    public ChatClient(String serverIP, IClientGUIControllerCallback newClientGUIController) {
        super(serverIP, 2000);
        this.clientGUIController = newClientGUIController;
        this.messageHandler = new MessageHandler(this);
    }

    @Override
    public void processMessage(String pMessage) {
        messageHandler.handleServerNachricht(pMessage);
    }

    /**
     * Sendet eine Nachricht zum Server.
     *
     * @param nachricht Die Nachricht, die an den Server gesendet werden soll.
     */
    public void sendeNachricht(String nachricht) {
        // Falls die Nachricht noch kein Befehl ist, so wird sie als Nachricht deklariert.
        if (!nachricht.startsWith("/")) {
            nachricht = "/nachricht " + nachricht;
        }
        this.send(nachricht);
    }

    /**
     * Sendet an den Server, dass der Nutzer seinen Raum wechselt.
     *
     * @param channelName Der Name des neuen Channels.
     */
    public void betreteRaum(String channelName) {
        this.send("/wechsleChannel " + channelName.trim());
    }

    /**
     * Sendet an den Server, dass der Benutzer einen neuen Channel erstellt.
     *
     * @param channelName   Der Name des neuen Channels.
     * @param elternChannel Der Name der Eltern des neuen Channels.
     */
    public void erstelleChannel(String channelName, String elternChannel) {
        this.send("/erstelleChannel " + channelName.trim() + " " + elternChannel);
    }

    /**
     * Sendet an den Server, dass der Benutzer einen Channel loescht.
     *
     * @param channelName Der Name des Channels, der geloescht werden soll.
     */
    public void loescheChannel(String channelName) {
        this.send(("/loescheChannel " + channelName.trim()));
    }


    @Override
    public void druckeText(String text) {
        clientGUIController.schreibeText(text);
    }


    @Override
    public void setzeChannelName(String channelName) {
        clientGUIController.setzeNeuenChannel(channelName);
    }


    @Override
    public void updateChannelErstellen(String channelName) {
        String[] channelPair = channelName.split(" ");
        clientGUIController.channelHinzufuegen(channelPair[0], channelPair[1]);
    }


    @Override
    public void updateChannelLoeschen(String channelName) {
        clientGUIController.channelLoeschen(channelName);
    }


    @Override
    public void updateBenutzerBetreten(String benutzerName) {
        clientGUIController.benutzerZuListeHinzufuegen(benutzerName);
    }


    @Override
    public void updateBenutzerVerlassen(String benutzerName) {
        clientGUIController.benutzerAusChannelLoeschen(benutzerName);
    }

    @Override
    public void updateBenutzerListe(String benutzerListe) {
        clientGUIController.updateUserList(benutzerListe);
    }

    @Override
    public void bearbeiteLoginFehler() {
        clientGUIController.setzeLoginZurueck();
    }

    @Override
    public void bearbeiteAusloggen() {
        clientGUIController.ausgeloggt();
    }

    /**
     * Sendet die Nutzerdaten zum Einloggen an den Server weiter..
     *
     * @param nutzerName     Der Name des Benutzers.
     * @param nutzerPasswort Das Passwort des Benutzers.
     */
    public void einloggen(String nutzerName, String nutzerPasswort) {
        this.send("/authentifizieren " + nutzerName + " " + nutzerPasswort);
    }

    /**
     * Sendet den Ausloggen Befehl an den Server.
     */
    public void ausloggen() {
        this.send("/ausloggen");
        if (this.istVerbunden()) {
            druckeText("Verbindung getrennt!");
        }
    }

    /**
     * Trennt die Verbindung mit dem Server.
     */
    public void trenneVerbindung() {
        this.ausloggen();
        this.close();
    }
}
