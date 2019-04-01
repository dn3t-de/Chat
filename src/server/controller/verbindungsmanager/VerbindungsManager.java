package server.controller.verbindungsmanager;

import server.controller.messagehandler.IMessageHandlerCallback;
import za.netzklassen.Server;

import java.util.logging.Logger;

/**
 * Verwaltet die Verbindungen zu den Clients und sendet/empfängt die Nachrichten, die von/zu den Clients gesendet werden.
 */
public class VerbindungsManager extends Server implements IVerbindungsManager {

    private static final Logger LOGGER = Logger.getLogger(VerbindungsManager.class.getName());

    private static final int SERVER_PORT = 2000;

    private final IMessageHandlerCallback messageHandler;

    /**
     * Konstruktor
     *
     * @param messageHandlerCallback Messagehandler mit dem kommuniziert werden soll.
     */
    public VerbindungsManager(IMessageHandlerCallback messageHandlerCallback) {
        super(SERVER_PORT);
        this.messageHandler = messageHandlerCallback;
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        LOGGER.info("Processing message: \"" + pMessage + "\" from client: " + pClientIP + ":" + pClientPort);
        messageHandler.handleClientNachricht(pClientIP, pClientPort, pMessage);
    }

    @Override
    public void sendeNachricht(String ipAdresse, int port, String nachricht) {
        LOGGER.info("Sending message: \"" + nachricht + "\" from client: " + ipAdresse + ":" + port);
        this.send(ipAdresse, port, nachricht);
    }

    @Override
    public void closeConnection(String pClientIP, int pClientPort) {
        super.closeConnection(pClientIP, pClientPort);
        LOGGER.info("Closed connection to client: " + pClientIP + ":" + pClientPort);
        messageHandler.handleClientNachricht(pClientIP, pClientPort, "/ausloggen");
    }

    @Override
    public void close() {
        super.close();
    }
}
