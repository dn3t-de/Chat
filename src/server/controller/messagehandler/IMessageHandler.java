package server.controller.messagehandler;

import common.ListExt.ListExt;
import common.UpdateTyp;
import server.data.ClientInfo;

/**
 * Interface, das den MessageHandler des Servers darstellt.
 */
public interface IMessageHandler {

    /**
     * Sendet eine Information einen Client.
     *
     * @param client    Client an den die Nachricht gesendet werden soll.
     * @param nachricht Nachricht, die an den Client gesendet wird.
     */
    void sendeInfo(ClientInfo client, String nachricht);

    /**
     * Sendet eine Information an eine Liste von Clients.
     *
     * @param clients   Clients an die die Nachricht gesendet werden soll.
     * @param nachricht Nachricht, die die Cientd erhalten sollen.
     */
    void sendeInfo(ListExt<ClientInfo> clients, String nachricht);

    /**
     * Sendet eine Warnung an einen Client.
     *
     * @param client    Client an den die Warnung gehen soll.
     * @param nachricht Nachricht, die der Client erhalten soll.
     */
    void sendeWarnung(ClientInfo client, String nachricht);

    /**
     * Sendet ein Update an einen Client.
     *
     * @param client    Client, der das Update erhalten soll.
     * @param updateTyp Art des Updates.
     * @param befehl    Befehl der ausgefuehrt werden soll.
     */
    void sendeUpdate(ClientInfo client, UpdateTyp updateTyp, String befehl);

    /**
     * Sendet ein Update an eine Liste von Clients.
     *
     * @param clients   Clients, die das Update erhalten sollen.
     * @param updateTyp Art des Updates.
     * @param befehl    Befehl, den die Clients ausfuehren sollen.
     */
    void sendeUpdate(ListExt<ClientInfo> clients, UpdateTyp updateTyp, String befehl);

    /**
     * Sendet eine Nachricht an eine Liste von Clients.
     *
     * @param clients   Cleints, die die Nachricht erhalten sollen.
     * @param nachricht Nachricht, die gesendet wird.
     */
    void sendeNachricht(ListExt<ClientInfo> clients, String nachricht);

    /**
     * Beenden der Verbindung.
     */
    void close();
}
