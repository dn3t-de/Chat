package client.controller.MessageHandler;

/**
 * Interface, das den MessageHandler des Clients darstellt.
 */
public interface IMessageHandler {

    /**
     * Wertet die ankommenden Nachrichten aus und gibt sie als Befehl weiter..
     *
     * @param pMessage Nachicht, die vom Server eintrifft.
     */
    void handleServerNachricht(String pMessage);
}
