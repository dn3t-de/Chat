package server.controller.messagehandler;

import common.ListExt.ListExt;
import common.UpdateTyp;
import server.controller.chatserver.IChatServerCallback;
import server.controller.verbindungsmanager.IVerbindungsManager;
import server.controller.verbindungsmanager.VerbindungsManager;
import server.data.ClientInfo;

import java.util.logging.Logger;

/**
 * Wandelt Textnachrichten zu Methodenaufrufen um, die im ChatServer aufgerufen werden.
 */
public class MessageHandler implements IMessageHandler, IMessageHandlerCallback {

    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    private final IVerbindungsManager verbindungsManager;
    private final IChatServerCallback chatServer;

    public MessageHandler(final IChatServerCallback chatServerCallback) {
        this.chatServer = chatServerCallback;
        this.verbindungsManager = new VerbindungsManager(this);
    }


    @Override
    public void handleClientNachricht(String ipAdresse, int port, String nachricht) {

        LOGGER.info("Received message: \"" + nachricht + "\" from client: " + ipAdresse + ":" + port);

        ClientInfo ci = new ClientInfo(ipAdresse, port);

        // Anfang und Ende der Nachricht von Leerzeichen befreien
        nachricht = nachricht.trim();

        // String anhand von Leerzeichen aufsplitten
        String[] befehl = nachricht.split(" ");

        if (befehl.length > 0) {
            String inhalt = nachricht.replaceFirst(befehl[0] + " ", "");

            // Am Anfang steht der Befehl, daher wird das erste Wort verwendet
            switch (befehl[0]) {

                // authentifizieren <benutzerName> <passwort>
                case "/authentifizieren": {
                    if (befehl.length == 3) {
                        chatServer.einloggen(ci, befehl[1], befehl[2]);
                        break;
                    }
                }

                // nachricht <nachrichtenText>
                case "/nachricht": {
                    chatServer.nachrichtSenden(ci, inhalt);
                    break;
                }

                // channelWechseln <channelName>
                case "/wechsleChannel": {
                    if (befehl.length == 2) {
                        chatServer.channelWechseln(ci, befehl[1]);
                        break;
                    }
                }

                // channelErstellen <channelName> <elternChannel>
                case "/erstelleChannel": {
                    if (befehl.length == 3) {
                        chatServer.channelErstellen(ci, befehl[1], befehl[2]);
                        break;
                    }
                }

                // channelLoeschen <channelName>
                case "/loescheChannel": {
                    if (befehl.length == 2) {
                        chatServer.channelLoeschen(ci, befehl[1]);
                        break;
                    }
                }

                // ausloggen
                case "/ausloggen": {
                    if (befehl.length == 1) {
                        chatServer.ausloggen(ci);
                        break;
                    }
                }

                // Fehler ausgeben
                default:
                    sendeWarnung(ci, "Fehlerhafte Eingabe. Befehl nicht bekannt oder falsche Parameter.");
                    break;
            }
        }
    }


    @Override
    public void sendeInfo(ClientInfo client, String nachricht) {
        final String info = "/info " + nachricht;
        verbindungsManager.sendeNachricht(client.getIp(), client.getPort(), info);
    }

    @Override
    public void sendeInfo(ListExt<ClientInfo> clients, String nachricht) {
        for (ClientInfo ci : clients.getList()) {
            sendeInfo(ci, nachricht);
        }
    }

    @Override
    public void sendeWarnung(ClientInfo client, String nachricht) {
        final String warnung = "/warnung " + nachricht;
        verbindungsManager.sendeNachricht(client.getIp(), client.getPort(), warnung);
    }

    @Override
    public void sendeUpdate(ClientInfo client, UpdateTyp updateTyp, String parameter) {
        String update = updateTyp + " " + parameter;
        verbindungsManager.sendeNachricht(client.getIp(), client.getPort(), update);
    }

    @Override
    public void sendeUpdate(ListExt<ClientInfo> clients, UpdateTyp updateTyp, String parameter) {
        for (ClientInfo ci : clients.getList()) {
            sendeUpdate(ci, updateTyp, parameter);
        }
    }

    private void sendeNachricht(ClientInfo client, String nachricht) {
        String mitteilung = "/nachricht " + nachricht;
        verbindungsManager.sendeNachricht(client.getIp(), client.getPort(), mitteilung);
    }

    @Override
    public void sendeNachricht(ListExt<ClientInfo> clients, String nachricht) {
        for (ClientInfo ci : clients.getList()) {
            sendeNachricht(ci, nachricht);
        }
    }

    @Override
    public void close() {
        verbindungsManager.close();
    }
}
