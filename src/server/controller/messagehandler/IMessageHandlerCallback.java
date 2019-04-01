package server.controller.messagehandler;

/**
 * Interface der den MessageHandler beschreibt.
 */
public interface IMessageHandlerCallback {
    /**
     * Wertet Nachrichten der Clients aus und wandelt sie in einen Befehl für den ChatServer um.
     *
     * @param ipAdresse IP-Adresse des Clients.
     * @param port      Port des Clients.
     * @param nachricht Nachricht, die der Client gesendet hat.
     */
    void handleClientNachricht(String ipAdresse, int port, String nachricht);
}