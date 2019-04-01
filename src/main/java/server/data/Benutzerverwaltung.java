package server.data;

import common.ListExt.ListExt;

/**
 * Klasse zum Aufbauen und Verwalten der Benutzerliste.
 */
public class Benutzerverwaltung {

    // Allgemeine Benutzerliste
    public static ListExt<Benutzer> benutzerliste = new ListExt<>();

    /**
     * Fuegt einen Benutzer der Liste hinzu.
     *
     * @param nutzerName    Name des neuen Benutzers
     * @param neuesPasswort Passwort des neuen Benutzers
     */
    public static void benutzerHinzufuegen(String nutzerName, String neuesPasswort) {
        if (!existiertNutzer(nutzerName.toLowerCase())) {
            benutzerliste.add(new Benutzer(nutzerName.toLowerCase(), neuesPasswort));
        }
    }

    /**
     * Ueberprueft, ob der uebergebe Benutezr mit dem Passwort in der Benutzerliste eingetragen ist.
     *
     * @param nutzer   Nutzername des zu ueberpruefenden Nutzers.
     * @param passwort Passwort des zu ueberpruefenden Nutzers.
     * @return true, wenn Benutzername und Passwort uebereinstimmen.
     */
    public static boolean ueberpruefeNutzer(String nutzer, String passwort) {
        benutzerliste.toFirst();
        while (benutzerliste.hasAccess()) {
            if (benutzerliste.getContent().getName().equalsIgnoreCase(nutzer) & benutzerliste.getContent().getPasswort().equals((passwort))) {
                return true;
            }
            benutzerliste.next();
        }
        return false;
    }

    /**
     * Ueberprueft, ob es den Nutzer schon in der Liste gibt.
     *
     * @param nutzerName Name des Nutzers, der ueberprueft werden soll.
     * @return true, wenn schon ein Nutzer mit dem Namen existiert.
     */
    private static boolean existiertNutzer(String nutzerName) {
        benutzerliste.toFirst();
        while (benutzerliste.hasAccess()) {
            if (benutzerliste.getContent().getName().equals(nutzerName)) {
                return true;
            }
            benutzerliste.next();
        }
        return false;
    }

    /**
     * Loescht den Nutzer mit dem uebergebenen Nutzernamen.
     *
     * @param nutzerName Name des Benutzers der geloescht werden wird.
     * @return true, wenn der Nutzer geloescht weurde.
     */
    public static boolean loescheNutzer(String nutzerName) {
        if (!existiertNutzer(nutzerName)) {
            return false;
        }
        benutzerliste.toFirst();
        while (benutzerliste.hasAccess()) {
            if (benutzerliste.getContent().getName().equals(nutzerName)) {
                benutzerliste.remove();
                return true;
            }
            benutzerliste.next();
        }
        return false;
    }

    /**
     * Speichert die Benutzerliste in die XML Datei.
     *
     * @return true, wenn es in XMLParser.speichereBenutzer() keine Fehler gab, sonst false
     */
    public static boolean speichereBenutzer() {
        return XMLParser.speichereBenutzer();
    }

    /**
     * Innere Klasse fuer Benutzer.
     */
    public static class Benutzer {
        // Variablen der Benutzerklasse
        private final String name;
        private final String passwort;

        /**
         * Konstruktor fuer die Benutzer Klasse.
         *
         * @param name     Benutzername des neuen Benutezrs.
         * @param passwort Passwort des neuen Benutzers.
         */
        public Benutzer(String name, String passwort) {
            this.name = name;
            this.passwort = passwort;
        }

        /**
         * GETTER fuer den Namen des Benutezrs.
         *
         * @return Name des Nutezrs.
         */
        public String getName() {
            return this.name;
        }

        /**
         * GETTER fuer das Passwort des Benutezrs.
         *
         * @return Passwort des Nutzers.
         */
        public String getPasswort() {
            return this.passwort;
        }
    }
}