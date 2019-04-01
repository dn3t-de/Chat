package server.data;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import za.graph.Vertex;

import java.io.*;
import java.util.List;

/**
 * Klasse zum Einlesen der Channel- und Benutzerstruktur aus einer XML Datei.
 * Die Channels und Benutzer werden nach dem Einlesen als Objekte der Klasse Channel gespeichert.
 */
public class XMLParser {

    /**
     * Liest die Channelstruktur aus einer fest definierten XML-Datei aus.
     *
     * @return true, wenn es keine Fehler gab, sonst false.
     */
    public static boolean channelsEinlesen() {
        try {
            Document xmlChannellist = new SAXBuilder().build("data" + File.separator + "Channels.xml");

            Element rootChannel = xmlChannellist.getRootElement();
            List<Element> channelList = rootChannel.getChildren("Channel");

            for (Element newchannel : channelList) {
                Channelverwaltung.channelHinzufuegen(newchannel.getChildText("Name"), newchannel.getChildText("Eltern"));
            }
            return true;
            // im Fehlerfall wird false zurueckgegeben
        } catch (JDOMException | IOException e) {
            return false;
        }
    }

    /**
     * Liest die Benutzerliste aus einer fest definierten XML-Datei aus.
     *
     * @return true, wenn es keine Fehler gab, sonst false.
     */
    public static boolean benutzerEinlesen() {
        try {
            Document xmlBenutzerlist = new SAXBuilder().build("data" + File.separator + "Benutzer.xml");

            Element rootElement = xmlBenutzerlist.getRootElement();
            List<Element> BenutzerList = rootElement.getChildren("Nutzer");

            for (Element newNutzer : BenutzerList) {
                Benutzerverwaltung.benutzerHinzufuegen(newNutzer.getChildText("Name"), newNutzer.getChildText("Passwort"));
            }
            return true;
            // im Fehlerfall wird false zurueckgegeben
        } catch (JDOMException | IOException e) {
            return false;
        }
    }

    /**
     * Speichert die Benutzerliste in einer fest definierten XML-Datei.
     *
     * @return true, wenn es keine Fehler gab, sonst false.
     */
    public static boolean speichereBenutzer() {
        Document doc;
        try {
            // Datei leeren um sie dann neu zu schreiben
            doc = new SAXBuilder().build("data" + File.separator + "Benutzer.xml");
            doc.removeContent(doc.getRootElement());
            Element benutzer = new Element("Benutzer");

            // Durchlaufe die Liste der Benutzer und speichere sie in Element
            Benutzerverwaltung.benutzerliste.toFirst();
            // Benutzerliste durchlaufen
            while (Benutzerverwaltung.benutzerliste.hasAccess()) {
                Element nutzer = new Element("Nutzer");

                // Nutzername und -passwort auslesen
                Element nutzerName = new Element("Name");
                nutzerName.addContent(Benutzerverwaltung.benutzerliste.getContent().getName());
                Element nutzerPasswort = new Element("Passwort");
                nutzerPasswort.addContent(Benutzerverwaltung.benutzerliste.getContent().getPasswort());

                // Name und Passwort zum Nutzer hinzufuegen
                nutzer.addContent(nutzerName);
                nutzer.addContent(nutzerPasswort);
                // Nutzer zu Benutzer hinzufuegen
                benutzer.addContent(nutzer);

                // zum naechsten Element der Liste gehen
                Benutzerverwaltung.benutzerliste.next();
            }
            // Benutzer als Rootknoten der XML setzen
            doc.setRootElement(benutzer);

            XMLOutputter out = new XMLOutputter();
            out.setFormat(Format.getPrettyFormat());

            // XML speichern
            out.output(doc, new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data" + File.separator + "Benutzer.xml"), "UTF-8")));

            return true;

            // im Fehlerfall wird false zurueckgegeben
        } catch (JDOMException | IOException e) {
            return false;
        }
    }

    /**
     * Speichert die Channelliste in einer fest definierten XML-Datei.
     *
     * @return true, wenn es keine Fehler gab, sonst false.
     */
    public static boolean speichereChannels() {
        Document doc;
        try {
            doc = new SAXBuilder().build("data" + File.separator + "Channels.xml");
            doc.removeContent(doc.getRootElement());
            Element channels = new Element("Channels");

            za.List<Vertex> vertices = Channelverwaltung.getChannelGraph().getVertices();
            vertices.toFirst();

            while (vertices.hasAccess()) {
                if (vertices.getContent().getWert().getName().equals("ROOT")) {
                    vertices.next();
                } else {
                    Element channel = new Element("Channel");

                    Element channelName = new Element("Name");
                    channelName.addContent(vertices.getContent().getWert().getName());
                    Element channelEltern = new Element("Eltern");
                    channelEltern.addContent(vertices.getContent().getWert().getEltern());

                    channel.addContent(channelName);
                    channel.addContent(channelEltern);

                    channels.addContent(channel);

                    vertices.next();
                }
            }
            doc.setRootElement(channels);

            XMLOutputter out = new XMLOutputter();
            out.setFormat(Format.getPrettyFormat());

            // XML speichern
            out.output(doc, new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data" + File.separator + "Channels.xml"), "UTF-8")));

            return true;

            // im Fehlerfall wird false zurueckgegeben
        } catch (JDOMException | IOException e) {
            return false;
        }
    }
}
