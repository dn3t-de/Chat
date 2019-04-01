package server.data;

/**
 * Klasse die Informationen zu einem Client speichert.
 */
public class ClientInfo {
    // Variablen
    private final String ip;
    private final int port;

    /**
     * Konstruktor
     *
     * @param ip   IP von der aus der Clients verbunden ist.
     * @param port Port ueber den der Client erreichbar ist.
     */
    public ClientInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * GETTER der IP des Clients
     *
     * @return IP des Clients
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * GETTER des Ports des Clients
     *
     * @return Port des Clients
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Vergleicht das uebergebene mit dem aktuellen Objekt.
     *
     * @param obj Client der ueberprueft werden soll.
     * @return true, wenn es der Selbe ist
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ClientInfo)) {
            return false;
        } else {
            ClientInfo clientInfo = (ClientInfo) obj;
            String str1 = this.ip + ":" + this.port;
            String str2 = clientInfo.getIp() + ":" + clientInfo.getPort();
            return str1.equals(str2);
        }
    }

    /**
     * Erstellt einen String aus der IP und dem Port des Clients.
     *
     * @return String mit IP und Port des Clients
     */
    @Override
    public String toString() {
        return "ClientInfo: " + this.ip + ":" + this.port;
    }

    /**
     * Erstellt einen Hashcode fuer das aktuelle Objekt.
     *
     * @return gehashter Wert.
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash *= 13 * ip.hashCode();
        hash *= 37 * port;
        return hash;
    }
}
