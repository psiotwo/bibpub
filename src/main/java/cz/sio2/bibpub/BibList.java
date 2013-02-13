package cz.sio2.bibpub;

import com.liferay.faces.portal.context.LiferayFacesContext;
import org.jbibtex.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.portlet.*;
import java.io.*;
import java.net.URL;
import java.util.*;

@ManagedBean
@SessionScoped
public class BibList {

    private String bibtexIRI;

    private List<BibItemType> list;

    private BibTeXDatabase db;

    private List<BibTeXEntry> other;

    private Map<BibItemType, List<BibTeXEntry>> entries;

    public BibItemType[] getAll() {
        return BibItemType.values();
    }

    @PostConstruct
    public void init() {
        System.out.println("INITIALIZING ... ");
        list = new ArrayList<BibItemType>();

        final PortletPreferences prefs = getPortletRequest().getPreferences();
        bibtexIRI = prefs.getValue("bibtexIRI", null);
        setSections(prefs.getValue("sections", null));

        reload();
    }

    private void reload() {
        other = new ArrayList<BibTeXEntry>();
        entries = new HashMap<BibItemType, List<BibTeXEntry>>();
        for(BibItemType t : BibItemType.values()) {
            List<BibTeXEntry> list = new ArrayList<BibTeXEntry>();
            entries.put(t,list);
        }

        try {
            db = parseBibTeX(bibtexIRI);

            for(BibTeXEntry entry : db.getEntries().values()) {
                BibItemType type = BibItemType.forType(entry.getType().getValue());
                if ( type == null) {
                    other.add(entry);
                } else {
                    entries.get(type).add(entry);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static
    public BibTeXDatabase parseBibTeX(String iri) throws IOException, ParseException {
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
            reader = new InputStreamReader(new URL(iri).openConnection().getInputStream());
            return parser.parse(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public String getField(BibTeXEntry entry, String x) {
        String formattedContent="";
        final Key key = new Key(x);
        Value value = entry.getField(key);

        if (value == null) {
            return null;
        }

        System.out.println("PARSING " + value.toUserString());

        try {
            String latexString = value.toUserString();
            List<LaTeXObject> objects = parseLaTeX(latexString);
            formattedContent = printLaTeX(objects);
        } catch (Exception e) {
            System.out.println("ERROR occured, skipping " + value.toUserString());
        }

        return formattedContent;
    }

    public String formatEntry(BibTeXEntry entry) {
        BibTeXFormatter f = new BibTeXFormatter();
                                       f.setIndent("   ");
        BibTeXDatabase db = new BibTeXDatabase();
        db.addObject(entry);

        StringWriter w = new StringWriter();

        try {
            f.format(db,w);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return w.toString();
    }

    static
    public List<LaTeXObject> parseLaTeX(String string) throws IOException, ParseException {
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

    public List<BibTeXEntry> getObjects(final String type) {
        BibItemType typeX = null;
        if ( type == null || ((typeX = BibItemType.forType(type)) == null) || (typeX == BibItemType.OTHER)) {
            return other;
        }

        return entries.get(typeX);

    }

    public void submit() {
        try {
            final PortletPreferences prefs = getPortletRequest().getPreferences();
            prefs.setValue("bibtexIRI", bibtexIRI);
            prefs.setValue("sections", getSections());

            // Save the preferences values
            prefs.store();
            //reload the list
            reload();

            // Switch the portlet to VIEW mode
//            ActionResponse actionResponse = (ActionResponse)(getPortletResponse());
//            actionResponse.setPortletMode(PortletMode.VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * Get PortletRequest within a JSF backing managed-bean
    */
    public PortletRequest getPortletRequest() {
        return LiferayFacesContext.getInstance().getPortletRequest();
    }

    /*
     * Get PortletResponse within a JSF backing managed-bean
     */
    public PortletResponse getPortletResponse() {
        return LiferayFacesContext.getInstance().getPortletResponse();
    }

    public String getBibtexIRI() {
        return bibtexIRI;
    }

    public void setBibtexIRI(String bibtexIRI) {
        this.bibtexIRI = bibtexIRI;
    }

    public void setSections(String sections) {
        list.clear();
        for( final String s : sections.split(",")) {
            BibItemType t = BibItemType.forType(s.trim());
            if ( t == null ) {
                continue;
            }
            list.add(t);
        }
    }

    public String getSections() {
        String x = "";
        boolean first = true;
        for( final BibItemType s : list) {
            if (!first) {
                x+=",";
            } else {
                first=false;
            }
            x += s.getType();
        }
        System.out.println("GET LIST " + x);
        return x;
    }

    public List<BibItemType> getSectionList() {
        return list;
    }

}
