package server.controller.verbindungsmanager;

/**
 * Interface, das den Verbindungsmanager beschreibt.
 */
public interface IVerbindungsManager {

    /**
     * Sendet eine Nachricht.
     *
     * @param ipAdresse IP-Adresse des Clients.
     * @param port      Port des Clients.
     * @param nachricht Nachricht die an den Client gesendet werden soll.
     */
    void sendeNachricht(String ipAdresse, int port, String nachricht);

    /**
     * Beendet die Verbindung.
     */
    void close();
}
