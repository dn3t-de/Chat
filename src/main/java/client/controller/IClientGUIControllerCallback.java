package client.controller;

/**
 * Interface, das alle Methoden zur Verfuegung stellt, die auf dem GUI aufgerufen werden koennen sollen.
 */
public interface IClientGUIControllerCallback {

    /**
     * Gibt den Text in der TextArea aus, in der die Chatnachrichten angezeigt werden.
     *
     * @param text Text der ausgegeben werden soll.
     */
    void schreibeText(String text);

    /**
     * Setzt einen neuen Text fuer das Label, das den aktuellen Channel anzeigt.
     *
     * @param channelName Name des neuen Channels.
     */
    void setzeNeuenChannel(String channelName);

    /**
     * Fuegt einen Channel der Liste der Channels hinzu.
     *
     * @param channelName   Name des neuen Channels.
     * @param elternChannel Elternchannel des neuen Channels.
     */
    void channelHinzufuegen(String channelName, String elternChannel);

    /**
     * Loescht einen Channel aus der Channelliste.
     *
     * @param channelName Name des Channels der geloescht werden soll.
     */
    void channelLoeschen(String channelName);

    /**
     * Fuegt einen Benutzer in die Liste der Benutzer ein.
     *
     * @param benutzerName Name des neuen Benutzers.
     */
    void benutzerZuListeHinzufuegen(String benutzerName);

    /**
     * Loescht einen Benutzer aus der Liste der Benutzer im aktuellen Channel.
     *
     * @param benutzerName Name des zu loeschenden Benutzers.
     */
    void benutzerAusChannelLoeschen(String benutzerName);

    /**
     * Fuegt eine Liste an Benutzern in die Benutzerliste ein.
     *
     * @param benutzerListe Liste der Benutzer mit "," getrennt.
     */
    void updateUserList(String benutzerListe);

    /**
     * Aktualisiert die GUI-Elemente nach einem Logoutfehler.
     */
    void setzeLoginZurueck();

    /**
     * Aktualisiert die GUI-Elemente nach dem Ausloggen.
     */
    void ausgeloggt();
}
