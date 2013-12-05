package cz.sio2.bibpub;

import com.liferay.faces.portal.context.LiferayFacesContext;
import org.jbibtex.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import java.io.*;
import java.net.URL;
import java.util.*;

@ManagedBean(name="bbbiblist")
public class BBBibList {

    private PortletPreferences getPreferences() {
        return LiferayFacesContext.getInstance().getPortletPreferences();
        //((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getPreferences();
    }

    public String getStoredProperty(final String key) {
        return getPreferences().getValue(key, null);
    }

    static
    public BibTeXDatabase parseBibTeX(InputStream is) throws IOException, ParseException {
        Reader reader = null;
        try {
            BibTeXParser parser = new BibTeXParser() {

                @Override
                public void checkStringResolution(Key key, BibTeXString string) {

                    if (string == null) {
                        System.err.println("Unresolved string: \"" + key.getValue() + "\"");
                    }
                }

                @Override
                public void checkCrossReferenceResolution(Key key, BibTeXEntry entry) {

                    if (entry == null) {
                        System.err.println("Unresolved cross-reference: \"" + key.getValue() + "\"");
                    }
                }
            };
            reader = new InputStreamReader(is);
            return parser.parse(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public String getField(BibTeXEntry entry, String x) {
        String formattedContent = "";
        final Key key = new Key(x);
        Value value = entry.getField(key);

        if (value == null) {
            return null;
        }

        try {
            String latexString = value.toUserString();
            List<LaTeXObject> objects = parseLaTeX(latexString);
            formattedContent = printLaTeX(objects);
        } catch (Exception e) {
            System.out.println("ERROR occured, skipping " + value.toUserString());
        }

        return formattedContent;
    }

    public static String formatEntry(BibTeXEntry entry) {
        BibTeXFormatter f = new BibTeXFormatter();
        f.setIndent("   ");
        BibTeXDatabase db = new BibTeXDatabase();
        db.addObject(entry);

        StringWriter w = new StringWriter();

        try {
            f.format(db, w);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return w.toString();
    }

    public String formatInHTML(BibTeXEntry entry) {
        return new RDFaReferenceFormatter().format(entry, true);
    }

    static
    public List<LaTeXObject> parseLaTeX(String string) throws IOException, ParseException {
        string = string.replaceAll("'", "v");

        Reader reader = new StringReader(string);

        try {
            LaTeXParser parser = new LaTeXParser();
            return parser.parse(reader);
        } finally {
            reader.close();
        }
    }

    static
    public String printLaTeX(List<LaTeXObject> objects) {
        return new LaTeXPrinter().print(objects);
    }

    public List<Key> parseTypes(String sections) {
        if (sections == null ) {
            return Collections.emptyList();
        }

        final List<Key> objects = new ArrayList<Key>();
        for ( String s: sections.split(",")) {
            objects.add(new Key(s));
        }
        return objects;
    }

    public List<BibTeXEntry> getAllObjectsInDescendingDate() {
        List<BibTeXEntry> entries = new ArrayList<BibTeXEntry>();
        try {
            final BibTeXDatabase db = parseBibTeX(new URL(getStoredProperty("bibtexIRI")).openConnection().getInputStream());

            final String sections =  getStoredProperty("sections");

            final Collection<Key> c = parseTypes(sections);

            for ( Key e : new HashMap<Key,BibTeXEntry>(db.getEntries()).keySet()) {
                BibTeXEntry ex = db.getEntries().get(e);
                if (!sections.isEmpty() && !c.contains(ex.getType())) {
                     db.removeObject(ex);
                }
            }
            entries = new ArrayList<BibTeXEntry>(db.getEntries().values());
            Collections.sort(entries, new Comparator<BibTeXEntry>() {

                public int compare(BibTeXEntry o1, BibTeXEntry o2) {
                    Value v1 = o1.getField(BibTeXEntry.KEY_YEAR);
                    Value v2 = o2.getField(BibTeXEntry.KEY_YEAR);
                    if (v1 == null) {
                        if (v2 == null) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        if (v2 == null) {
                            return -1;
                        } else {
                            return -v1.toUserString().compareTo(v2.toUserString());
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return entries;
    }
}
