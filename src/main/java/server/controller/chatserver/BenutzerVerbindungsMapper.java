package server.controller.chatserver;

import common.ListExt.ListExt;
import server.data.ClientInfo;

import java.util.HashMap;

/**
 * Diese Klasse stellt ein Mapping zwischen Benutzernamen und Verbindungen her.
 */
class BenutzerVerbindungsMapper {

    // Map von Benutzername auf Verbindung
    private static final HashMap<String, ClientInfo> benutzerVerbindungsMap = new HashMap<>();

    // Map von Verbindung auf Benutzername
    private static final HashMap<ClientInfo, String> verbindungsBenutzerMap = new HashMap<>();

    /**
     * Fuegt ein Mapping zwischen Benutzername und Verbindung (IP, Port) hinzu.
     *
     * @param benutzerName Benutzername des hinzuzufuegenden Eintrages/Mappings
     * @param clientInfo   Verbindung des hinzuzufuegenden Eintrages/Mappings
     */
    static void hinzufuegen(String benutzerName, ClientInfo clientInfo) {
        benutzerVerbindungsMap.put(benutzerName, clientInfo);
        verbindungsBenutzerMap.put(clientInfo, benutzerName);
    }

    /**
     * Fragt die Verbindung zum gegebenen Benutzernamen ab.
     *
     * @param benutzerName Benutzername des Nutzers zu dem die Verbindung abgefragt werden soll.
     * @return Verbingungsinformation zum gegebenen Benutzernamen wenn vorhanden, sonst null.
     */
    static ClientInfo getVerbindung(String benutzerName) {
        return benutzerVerbindungsMap.get(benutzerName);
    }

    /**
     * Fragt den Benutzernamen zur gegebenen Verbindung ab.
     *
     * @param clientInfo Verbindung zu der der Benutzername abgefragt werden soll.
     * @return Benutzername der zur gegebenen Verbindung hinterlegt ist - sofern vorhanden, Sonst null.
     */
    static String getBenutzer(ClientInfo clientInfo) {
        return verbindungsBenutzerMap.get(clientInfo);
    }

    /**
     * Entfernt das Benutzer/Verbindungs-Paar mit der gegebenen Verbindung aus dem Mapping.
     *
     * @param clientInfo Verbindung, die aus dem Mapping entfernt werden soll.
     */
    static void entfernen(ClientInfo clientInfo) {
        String benutzerName = verbindungsBenutzerMap.remove(clientInfo);
        benutzerVerbindungsMap.remove(benutzerName);
    }

    /**
     * Prueft, ob der gegebene Benutzername bereits im Mapping vorhanden ist.
     *
     * @param benutzerName Benutzername, auf den geprueft werden soll.
     * @return True, wenn der Benutzer hinterlegt ist, sonst false.
     */
    static boolean vorhanden(String benutzerName) {
        return benutzerVerbindungsMap.containsKey(benutzerName);
    }

    /**
     * Prueft, ob die gegebene Verbindung bereits im Mapping vorhanden ist.
     *
     * @param clientInfo Verbindung, auf die geprueft werden soll.
     * @return True, wenn die Verbindung hinterlegt ist, sonst false.
     */
    static boolean vorhanden(ClientInfo clientInfo) {
        return verbindungsBenutzerMap.containsKey(clientInfo);
    }

    /**
     * Prueft, ob die Verbindung mit den gegebenen Informationen im Mapping vorhanden ist.
     *
     * @param ipAdresse IP-Adresse der Verbindung
     * @param port      Port der Verbindung
     * @return True, wenn die Verbindung im Mapping vorhanden ist, sonst false.
     */
    static boolean vorhanden(String ipAdresse, int port) {
        return vorhanden(new ClientInfo(ipAdresse, port));
    }

    /**
     * Fragt die aktuelle Benutzerliste als Komma-separierten String ab.
     *
     * @return Liste der Benutzer als String - getrennt durch Kommata (z.B. "Alice,Bob,Charlie")
     */
    static String getBenutzerListeAsString() {
        StringBuilder benutzerListe = new StringBuilder();
        for (String benutzer : benutzerVerbindungsMap.keySet()) {
            benutzerListe.append(benutzer);
            benutzerListe.append(" ");
        }
        return benutzerListe.toString().trim().replace(' ', ',');
    }

    /**
     * Fragt eine Liste der aktuellen Verbindungen ab.
     *
     * @return Liste der aktuell hinterlegten Verbindungen.
     */
    static ListExt<ClientInfo> getVerbindungsListe() {
        ListExt<ClientInfo> verbindungsListe = new ListExt<>();
        for (ClientInfo verbindung : verbindungsBenutzerMap.keySet()) {
            verbindungsListe.add(verbindung);
        }
        return verbindungsListe;
    }

    /**
     * Fragt eine Liste der Verbindungen zu den uebergebenen Benutzernamen ab.
     *
     * @param benutzer Liste der Benutzernamen, zu denen die Verbindungen zurueckgegeben werden sollen.
     * @return Liste der Verbindungen, die zu den uebergebenen Benutzernamen hinterlegt sind.
     */
    static ListExt<ClientInfo> getVerbindungen(ListExt<String> benutzer) {
        ListExt<ClientInfo> verbindungen = new ListExt<>();
        ClientInfo verbindung;
        for (String nutzer : benutzer.getList()) {
            verbindung = BenutzerVerbindungsMapper.getVerbindung(nutzer);
            if (verbindung != null) {
                verbindungen.add(verbindung);
            }
        }
        return verbindungen;
    }
}
