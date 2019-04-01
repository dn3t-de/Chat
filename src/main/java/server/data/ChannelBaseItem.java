package server.data;

/**
 * Beschreibt einen Channel mit Verweis auf den ihm übergeordneten Channel.
 */
public class ChannelBaseItem {

    // Variablen der ChannelBaseItem Klasse
    private final String name;
    private final String eltern;

    /**
     * Konstruktor fuer die ChannelBaseItem Klasse
     *
     * @param name   Name des neuen ChannelBaseItems
     * @param eltern Elternname des neuen ChannelBaseItems
     */
    ChannelBaseItem(String name, String eltern) {
        this.name = name;
        this.eltern = eltern;
    }

    /**
     * Gibt den Namen des Channels zurueck.
     *
     * @return Name des Channels.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gibt den Namen des Elternchannels zurueck. "ROOT", falls es ein
     * uebergeordneter Channel ist.
     *
     * @return Elternchannel des Channels, "ROOT" wenn es ein
     * uebergeordneter Channel ist.
     */
    public String getEltern() {
        return eltern;
    }
}
