package server.data;

import common.ListExt.ListExt;
import za.List;
import za.graph.Edge;
import za.graph.Graph;
import za.graph.Vertex;

/**
 * Verwaltet die Channels, welche in einer Graph-Datenstruktur gehalten werden.
 */
public class Channelverwaltung {
    // Graph der die Channels enthaelt
    private static Graph channelGraph = new Graph();

    // Fuegt das ROOT Item in den Graphen ein
    static {
        channelGraph.addVertex(new Vertex("ROOT", new ChannelItem("ROOT", "")));
    }

    /**
     * Fuegt einen Channel in den Graphen hinzu.
     *
     * @param channelName   Name des neuen Channels
     * @param channelEltern Name des Elternchannels
     * @return true, wenn der Channel hinzugefuegt wurde
     */
    public static boolean channelHinzufuegen(String channelName, String channelEltern) {
        if (!existiertChannel(channelName)) {
            // neues ChannelItem anlegen fuer den neuen Graphenknoten
            ChannelItem newChannel = new ChannelItem(channelName, channelEltern);
            // ChannelItem als Knoten zum Graphen hinzufuegen
            channelGraph.addVertex(new Vertex(channelName, newChannel));
            // Neue Kante erstellen zwischen Elternknoten und neuem Koten
            channelGraph.addEdge(new Edge(channelGraph.getVertex(channelEltern), channelGraph.getVertex(channelName)));
            return true;
        }
        return false;
    }

    /**
     * Prueft, ob der Channel mit dem uebergebenen Namen existiert.
     *
     * @param channelName Name des zu ueberpruefenden Channels.
     * @return true, wenn ein Channel mit dem uebergebenen Namen existiert.
     */
    private static boolean existiertChannel(String channelName) {
        if (channelGraph.getVertices().isEmpty()) {
            return false;
        }

        Vertex gesuchterKnoten = channelGraph.getVertex(channelName);
        return gesuchterKnoten != null;
    }

    /**
     * Erstellt einen neuen Graphen mit ROOT Element
     */
    public static void erstelleInitialenGraphen() {
        channelGraph = new Graph();
        channelGraph.addVertex(new Vertex("ROOT", new ChannelItem("ROOT", "")));
    }

    /**
     * Gibt eine Liste der Nutzer aus dem Channel zurueck
     *
     * @param channelName Name des Channels von dem die Nutzerliste zurueckgegeben werden soll.
     * @return Kopie der Nutzerliste der Nutzer, die im Channel sind.
     */
    public static ListExt<String> werIstImChannel(String channelName) {
        ListExt<String> nutzerInChannel = new ListExt<>();
        if (channelName == null) {
            return nutzerInChannel;
        }
        ListExt<String> schleifenListe = channelGraph.getVertex(channelName).getWert().getNutzerImChannel();
        schleifenListe.toFirst();
        while (schleifenListe.hasAccess()) {
            nutzerInChannel.add(schleifenListe.getContent());
            schleifenListe.next();
        }
        return nutzerInChannel;
    }

    /**
     * Gibt eine Liste der Nutzer aus, die ausser dem uebergebenen Nutzer im Channel sind.
     *
     * @param channelName  Name des Channels von dem die Nutzerliste ausgegeben werden soll.
     * @param benutzerName Name des Nutzers, der nicht in der Liste auftauchen soll.
     * @return Kopie der Liste der Nutzer im Channel ohne den Namen des uebergebenen Nutzers.
     */
    public static ListExt<String> werIstSonstImChannel(String channelName, String benutzerName) {
        ListExt<String> nutzerListe = werIstImChannel(channelName);
        nutzerListe.remove(benutzerName);
        return nutzerListe;
    }

    /**
     * Sucht den Channelnamen in dem sich der uebergebene Nutzer aufhaelt.
     *
     * @param nutzerName Nutzername der gesucht wird.
     * @return Channelname des Channels in dem sich der Nutzer aufhaelt, wenn sich der Nutzer in keinem Channel aufhaelt, null
     */
    public static String woIstNutzer(String nutzerName) {
        List<Vertex> channels = channelGraph.getVertices();
        channels.toFirst();

        while (channels.hasAccess()) {
            List<String> benutzer = channels.getContent().getWert().getNutzerImChannel();
            if (!benutzer.isEmpty()) {
                benutzer.toFirst();
                while (benutzer.hasAccess()) {
                    if (benutzer.getContent().equals(nutzerName)) {
                        return channels.getContent().getWert().getName();
                    }
                    benutzer.next();
                }
            }

            channels.next();
        }
        return null;
    }

    /**
     * Der uebergebne Nutzer wird, wenn er in einem Channel ist, aus diesem entfernt und wird in einen neuen hinzugefuegt-
     *
     * @param channelName  Name des Channels in den der Nutzer wecheln soll.
     * @param benutzerName Name des Benutzers der den Channel wecheln soll.
     * @return true, wenn der Nutzer in den neuen Channel eingefuegt wurde.
     */
    public static boolean betreteChannel(String channelName, String benutzerName) {
        if (!existiertChannel(channelName)) {
            return false;
        }

        List<Vertex> channels = channelGraph.getVertices();
        channels.toFirst();
        while (channels.hasAccess()) {

            if (channels.getContent().getWert().getName().equals(channelName)) {
                // Nutzer aus altem Channel loeschen
                String alterChannel = woIstNutzer(benutzerName);
                if (alterChannel != null) {
                    List<Vertex> channelListe = channelGraph.getVertices();
                    channelListe.toFirst();
                    while (channelListe.hasAccess()) {
                        if (channelListe.getContent().getWert().getName().equals(alterChannel)) {
                            loescheBenutzer(benutzerName);
                        }
                        channelListe.next();
                    }
                }
                // Nutzer in neuen Channel hinzufuegen
                channelGraph.getVertex(channelName).getWert().nutzerHinzufuegen(benutzerName);
                return true;
            }

            channels.next();
        }
        return false;
    }

    /**
     * Speichert die Channelliste in die XML Datei
     *
     * @return true, wenn es keine Fehler in XMLParser.speichereChannels() gab, sonst false.
     */
    public static boolean speichereChannels() {
        return XMLParser.speichereChannels();
    }

    /**
     * Prueft ob der angegebene Knoten ein Blattknoten im Graphen ist, also ob er nicht als Elternteil eines anderen auftritt
     *
     * @param channelName Name des Channels der ueberprueft werden soll
     * @return false, wenn er als Elternteil auftritt, sonst true
     */
    private static boolean istBlattKnoten(String channelName) {
        List<Vertex> channels = channelGraph.getVertices();
        channels.toFirst();
        while (channels.hasAccess()) {
            if (channels.getContent().getWert().getEltern().equals(channelName)) {
                return false;
            }
            channels.next();
        }
        return true;
    }

    /**
     * Prueft, ob im angegebenen Channel Nutzer angemeldet sind
     *
     * @param channelName Name des Channels der ueberprueft werden soll
     * @return false, wenn der Channel leer ist, sonst true
     */
    private static boolean hatAktiveNutzer(String channelName) {
        return !channelGraph.getVertex(channelName).getWert().getNutzerImChannel().isEmpty();
    }

    /**
     * Loescht den Channel, wenn er ein Blattknoten ist und keine aktiven Nutzer hat
     *
     * @param channelName Channelname des zu loeschenden Channels.
     * @return true, wenn der Channel geloescht wurde, sonst false
     */
    public static boolean loescheChannel(String channelName) {
        // loeschen darf nur gemacht werden, wenn kein Nutzer im Channel und der Knoten ein Blattknoten ist

        // Test ob der Knoten ein Blattknoten ist
        if (istBlattKnoten(channelName)) {
            if (!hatAktiveNutzer(channelName)) {
                channelGraph.removeVertex(channelGraph.getVertex(channelName));
                return true;
            }
        }
        return false;
    }

    /**
     * Ermittelt den Channel in dem sich der Nutzer befindet und loescht ihn dann aus der Nutzerlsit des Channels.
     *
     * @param nutzerName Name des Benutzers, der aus seinem Channel entfert werden soll.
     */
    public static void loescheBenutzer(String nutzerName) {
        String channel = woIstNutzer(nutzerName);
        Vertex vertex = channelGraph.getVertex(channel);
        if (vertex != null && vertex.getWert() != null) {
            ChannelItem ci = vertex.getWert();
            ci.entferneBenutzer(nutzerName);
            vertex.setWert(ci);
        }
    }

    /**
     * Ermittelt die Entfernung des uebergebenen Channels zum ROOT Element
     *
     * @param channelName Name des Channels der ueberprueft werden soll
     * @return Anzahl der Kanten zum ROOT Element
     */
    public static int distanzZuROOT(String channelName) {
        Vertex knoten = channelGraph.getVertex(channelName);

        if (channelName.equals("ROOT")) {
            return 0;
        } else {
            return 1 + distanzZuROOT(knoten.getWert().getEltern());
        }

    }

    /**
     * Gibt den Graphen als ListExt zurueck
     *
     * @return Graph als ListExt ChannelBaseItem
     */
    public static ListExt<ChannelBaseItem> getListe() {
        ListExt<ChannelBaseItem> graphList = new ListExt<>();

        List<Vertex> rootNachbarn = Channelverwaltung.channelGraph.getNeighbours(Channelverwaltung.channelGraph.getVertex("ROOT"));
        rootNachbarn.toFirst();
        while (rootNachbarn.hasAccess()) {
            erfasseGraph(rootNachbarn.getContent(), Channelverwaltung.channelGraph.getVertex("ROOT"), graphList);
            rootNachbarn.next();
        }

        return graphList;
    }

    /**
     * Hilfsfunktion, die den Graphen durchlaeuft und die Channels zu der uebergebenen Liste hinzufuegt
     *
     * @param aktuellerKnoten Vertex der untersucht wird
     * @param eltern          Elternknoten des aktuellenKnotens
     * @param graphList       Liste in die der Knoten eingetragen werden soll.
     */
    private static void erfasseGraph(Vertex aktuellerKnoten, Vertex eltern, ListExt<ChannelBaseItem> graphList) {
        String rootName = aktuellerKnoten.getWert().getName();

        graphList.add(new ChannelBaseItem(rootName, eltern.getWert().getName()));
        List<Vertex> nachbarn = Channelverwaltung.channelGraph.getNeighbours(aktuellerKnoten);

        nachbarn.toFirst();
        if (!nachbarn.isEmpty()) {
            while (nachbarn.hasAccess()) {

                if (!nachbarn.getContent().getWert().getName().equals(eltern.getWert().getName())) {
                    erfasseGraph(nachbarn.getContent(), aktuellerKnoten, graphList);

                }
                nachbarn.next();
            }
        }
    }

    public static Graph getChannelGraph() {
        return channelGraph;
    }

}
