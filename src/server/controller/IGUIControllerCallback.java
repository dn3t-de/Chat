package server.controller;

/**
 * Interface, dass fuer den GUI-Controller.
 */
public interface IGUIControllerCallback {
    /**
     * Aktualisiert die ChannelListView.
     */
    void aktualisiereChannelListView();

    /**
     * Aktualisiert die Liste der Benutzer.
     */
    void aktualisiereNutzerListView();

    /**
     * Beendet den Server.
     */
    void beendeServer();
}
