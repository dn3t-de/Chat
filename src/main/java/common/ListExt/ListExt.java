package common.ListExt;

import za.List;

import java.util.ArrayList;

/**
 * Erweiterung des List-Typs der ZA-Klassen, die einige zusaetzliche Funktionen hinzufuegt.
 *
 * @param <ContentType> generischer Typ.
 */
public class ListExt<ContentType> extends List<ContentType> {

    /**
     * Prueft, ob das gegebene Objekt in der Liste enthalten ist.
     *
     * @param object Objekt auf das Geprueft werden soll
     * @return True falls das Objekt in der Liste existiert, sonst false.
     */
    public boolean contains(ContentType object) {
        this.toFirst();

        while (this.hasAccess()) {
            ContentType content = this.getContent();
            if (content != null && content.equals(object)) {
                return true;
            }
            this.next();
        }
        return false;
    }

    /**
     * Fuegt das gegebene Objekt in die Liste ein wenn es noch nicht darin enthalten ist.
     *
     * @param object Das einzufuegende Objekt
     */
    public void add(ContentType object) {
        if (!this.contains(object)) {
            this.append(object);
        }
    }

    /**
     * Entfernt das gegebene Objekt aus der Liste.
     *
     * @param object Das Objekt, das aus der Liste entfernt werden soll
     */
    public void remove(ContentType object) {
        this.toFirst();
        while (this.hasAccess()) {
            ContentType content = this.getContent();
            if (content != null && content.equals(object)) {
                this.remove();
                return;
            }
            this.next();
        }
    }

    /**
     * Gibt die Liste als Java-Liste zurueck, um foreach-Konstrukte verwenden zu koennen.
     *
     * @return Aktuelle Liste als Java-Liste
     */
    public java.util.List<ContentType> getList() {
        ArrayList<ContentType> newList = new ArrayList<>();

        this.toFirst();
        while (this.hasAccess()) {
            newList.add(this.getContent());
            this.next();
        }

        return newList;
    }

    /**
     * Erzeugt aus den Listenelementen einen String in dem die Listenelemente mit einem "," getrennt sind.
     *
     * @return String, der Listenelemente mit "," getrennt enthaelt.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.toFirst();
        while (this.hasAccess()) {
            sb.append(this.getContent().toString());
            sb.append(" ");
            this.next();
        }
        return sb.toString().trim().replace(' ', ',');
    }
}
