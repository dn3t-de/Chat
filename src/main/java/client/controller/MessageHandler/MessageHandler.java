package client.controller.MessageHandler;

import client.controller.chatClient.IChatClientCallback;

/**
 * Implementiert Interface IMessageHandler und fuehrt die Operationen auf dem Client aus.
 */
public class MessageHandler implements IMessageHandler {
    // Chatclient von dem aus der MessageHandler erzeugt wurde.
    private final IChatClientCallback chatClient;

    /**
     * Konstruktor
     * Speichert den ChatClient.
     *
     * @param newChatClient Der ChatClient, von dem aus der MessageHandler aufgerufen wurde.
     */
    public MessageHandler(IChatClientCallback newChatClient) {
        this.chatClient = newChatClient;
    }

    @Override
    public void handleServerNachricht(String pMessage) {
        pMessage = pMessage.trim();
        String[] befehl = pMessage.split(" ");

        if (befehl.length > 0) {
            String inhalt = pMessage.replaceFirst(befehl[0] + " ", "");

            // Am Anfang steht der Befehl, daher wird das erste Wort verwendet
            switch (befehl[0]) {

                case ("/info"): {
                    if (inhalt.equals("Ausgeloggt.")) {
                        chatClient.bearbeiteAusloggen();
                    }
                    chatClient.druckeText("INFO: " + inhalt);
                    break;
                }

                case ("/warnung"): {
                    if (inhalt.equals("Fehler beim Einloggen.")) {
                        chatClient.bearbeiteLoginFehler();
                    }
                    chatClient.druckeText("WARNUNG: " + inhalt);
                    break;
                }

                case ("/nachricht"): {
                    chatClient.druckeText(inhalt);
                    break;
                }

                case ("/channelName"): {
                    chatClient.setzeChannelName(inhalt);
                    break;
                }

                case ("/channelErstellen"): {
                    chatClient.updateChannelErstellen(inhalt);
                    break;
                }

                case ("/channelLoeschen"): {
                    chatClient.updateChannelLoeschen(inhalt);
                    break;
                }

                case ("/benutzerBetreten"): {
                    chatClient.updateBenutzerBetreten(inhalt);
                    break;
                }

                case ("/benutzerVerlassen"): {
                    chatClient.updateBenutzerVerlassen(inhalt);
                    break;
                }

                case ("/benutzerListe"): {
                    if (befehl.length == 2) {
                        chatClient.updateBenutzerListe(inhalt);
                    }
                    break;
                }

                // Fehler ausgeben
                default:
                    chatClient.druckeText("Befehl wurde nicht erkannt");
                    break;
            }
        }
    }
}
