package client.controller.chatClient;

/**
 * Interface, das alle Methoden zur Verfuegung stellt, die vom MessageHandler aus aufgerufen werden koennen sollen.
 */
public interface IChatClientCallback {

    /**
     * Gibt einen Text auf dem GUI aus.
     *
     * @param text Text der ausgegeben werden soll.
     */
    void druckeText(String text);

    /**
     * Setzt den neuen Channelnamen in dem sich der Benutzer befindet.
     *
     * @param channelName Name des neuen Channels.
     */
    void setzeChannelName(String channelName);

    /**
     * Fuegt der Lsite der Channels einen Channel hinzu.
     *
     * @param channelName Name des neuen Channels.
     */
    void updateChannelErstellen(String channelName);

    /**
     * Loescht einen Channel aus der Liste.
     *
     * @param channelName Name des Channels der geloescht werden soll.
     */
    void updateChannelLoeschen(String channelName);

    /**
     * Fuegt einen Benutzer zu der Benutzerliste des aktuellen Channels hinzu.
     *
     * @param benutzerName Name des zu loeschenden Benutezrs.
     */
    void updateBenutzerBetreten(String benutzerName);

    /**
     * Loescht einen Benutzer aus der Liste, wenn er den Channel verlassen hat.
     *
     * @param benutzerName Name des zu loeschenden Benutzers.
     */
    void updateBenutzerVerlassen(String benutzerName);

    /**
     * Fuegt eine Liste an Benutzern in die Benutzerliste hinzu.
     *
     * @param benutzerListe Liste der Benutzer mit "," getrennt.
     */
    void updateBenutzerListe(String benutzerListe);

    /**
     * Repariert das GUI bei einem Loginfehler.
     */
    void bearbeiteLoginFehler();

    /**
     * Repariert das GUI, bei einem Logout.
     */
    void bearbeiteAusloggen();
}
