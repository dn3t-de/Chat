package server.controller.chatserver;

import server.data.ClientInfo;


/**
 * Interface, das alle Methoden zur Verfuegung stellt, die vom MessageHandler aus aufgerufen werden koennen sollen.
 */
public interface IChatServerCallback {
    /**
     * Loggt den Nutzer mit den gegebenen Informationen beim Server ein.
     *
     * @param client       Verbindungsinformation
     * @param benutzerName Benutzername
     * @param passwort     Passwort
     */
    void einloggen(ClientInfo client, String benutzerName, String passwort);

    /**
     * Loggt den Nutzer mit der gegebenen Verbindungsinformation beim Server aus.
     *
     * @param client Verbindungsinformation des veranlassenden Benutzers
     */
    void ausloggen(ClientInfo client);

    /**
     * Erstellt einen Channel auf dem Server.
     *
     * @param client        Verbindungsinformation des veranlassenden Benutzers
     * @param channelName   Name des zu erstellenden Channels.
     * @param elternChannel uebergeordneter Channel des zu erstellenden Channels.
     */
    void channelErstellen(ClientInfo client, String channelName, String elternChannel);

    /**
     * Loeschen den gegebenen Channel.
     *
     * @param client      Verbindungsinformation des veranlassenden Benutzers
     * @param channelName Name des zu loeschenden Channels
     */
    void channelLoeschen(ClientInfo client, String channelName);

    /**
     * Verschiebt den ausfuehrenden Benutzer in den Channel mit dem angegebenen Namen.
     *
     * @param client      Verbiundungsinformation des zu verschiebenden Benutzers
     * @param channelName Name des Channels in den der Benutzer wechseln moechte.
     */
    void channelWechseln(ClientInfo client, String channelName);

    /**
     * Sendet eine Nachricht.
     *
     * @param client    Verbindungsinformation des Nachricht sendenden Benutzers
     * @param nachricht Inhalt der Nachricht.
     */
    void nachrichtSenden(ClientInfo client, String nachricht);
}
