package server.data;

import common.ListExt.ListExt;

/**
 * Erweitert die ChannelBaseItem Klasse um eine Nutzerliste, der Benutzer, die sich im Channel befinden.
 */
public class ChannelItem extends ChannelBaseItem {
    // Variable der ChannelItem Klasse
    private final ListExt<String> nutzerImChannel;

    /**
     * Konstruktor der ChannelItem Klasse
     *
     * @param name   Name des neuen ChannelItems.
     * @param eltern Name des Elternchannels
     */
    ChannelItem(String name, String eltern) {
        super(name, eltern);
        nutzerImChannel = new ListExt<>();
    }

    /**
     * GETTER der Nutzerliste des Channels
     *
     * @return Liste der Nutzerliste des Channels
     */
    public ListExt<String> getNutzerImChannel() {
        return nutzerImChannel;
    }

    /**
     * Fuegt den Nutzer in den Channel ein
     *
     * @param neuerBenutzer Name des neuen Benutezrs im Channel.
     */
    public void nutzerHinzufuegen(String neuerBenutzer) {
        nutzerImChannel.add(neuerBenutzer);
    }

    /**
     * Entfernt den Benutzer aus dem Channel.
     *
     * @param alterBenutzer Name des Benutzers der geloescht werden soll.
     */
    public void entferneBenutzer(String alterBenutzer) {
        if (!nutzerImChannel.isEmpty()) {
            nutzerImChannel.toFirst();
            while (nutzerImChannel.hasAccess()) {
                if (nutzerImChannel.getContent().equals(alterBenutzer)) {
                    nutzerImChannel.remove();
                }
                nutzerImChannel.next();
            }
        }
    }
}
