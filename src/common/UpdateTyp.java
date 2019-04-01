package common;

/**
 * Alle moeglichen Updates, die als Nachrichten ueber das Server-Client-Protokoll gesendet werden koennen.
 */
public enum UpdateTyp {
    CHANNEL_ERSTELLEN("/channelErstellen"),
    CHANNEL_LOESCHEN("/channelLoeschen"),
    BENUTZER_BETRETEN("/benutzerBetreten"),
    BENUTZER_VERLASSEN("/benutzerVerlassen"),
    BENUTZER_LISTE("/benutzerListe"),
    CHANNEL_NAME("/channelName");

    private final String befehl;

    UpdateTyp(final String befehl) {
        this.befehl = befehl;
    }

    @Override
    public String toString() {
        return this.befehl;
    }
}
