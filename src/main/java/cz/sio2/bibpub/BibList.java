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

    private String sections;

    private Boolean renderCitation;

    private BibTeXDatabase bibTeXDatabase;

    private Map<BibItemType, List<BibTeXEntry>> namedEntries;
    private List<BibTeXEntry> otherEntries;

    private String getStoredProperty(final String key) {
        return LiferayFacesContext.getInstance().getPortletPreferences().getValue(key,null);
    }

    private void setStoredProperties(final Map<String,String> preferences) {
        final PortletPreferences prefs = LiferayFacesContext.getInstance().getPortletRequest().getPreferences();

        try {
            for (Map.Entry<String,String> entry : preferences.entrySet()) {
                prefs.setValue(entry.getKey(),entry.getValue());
            }

            // Save the preferences values
            prefs.store();
        } catch (ReadOnlyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ValidatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("INITIALIZING ... ");

        bibtexIRI = getStoredProperty("bibtexIRI");
        sections = getStoredProperty("sections");
        renderCitation = Boolean.parseBoolean(getStoredProperty("renderCitation"));

        reload();
    }

    private void reload() {
        otherEntries = new ArrayList<BibTeXEntry>();
        namedEntries = new HashMap<BibItemType, List<BibTeXEntry>>();
        for(BibItemType t : BibItemType.values()) {
            List<BibTeXEntry> list = new ArrayList<BibTeXEntry>();
            namedEntries.put(t, list);
        }

        try {
            bibTeXDatabase = parseBibTeX(bibtexIRI);

            for(BibTeXEntry entry : bibTeXDatabase.getEntries().values()) {
                BibItemType type = BibItemType.forType(entry.getType().getValue());
                if ( type == null) {
                    otherEntries.add(entry);
                } else {
                    namedEntries.get(type).add(entry);
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
        string = string.replaceAll("'","v");

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
            return otherEntries;
        }

        return namedEntries.get(typeX);

    }

    public void submit() {
        try {
            final Map<String,String> properties = new HashMap<String, String>();
            properties.put("bibtexIRI",bibtexIRI);
            properties.put("sections",sections);
            setStoredProperties(properties);

            reload();

            // Switch the portlet to VIEW mode
            LiferayFacesContext.getInstance().getActionResponse().setPortletMode(PortletMode.VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBibtexIRI() {
        return bibtexIRI;
    }

    public void setBibtexIRI(String bibtexIRI) {
        this.bibtexIRI = bibtexIRI;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public String getSections() {
        return sections;
    }

    public List<BibItemType> getSectionList() {
        final List<BibItemType> sectionList = new ArrayList<BibItemType>();
        for( final String s : sections.split(",")) {
            BibItemType t = BibItemType.forType(s.trim());
            if ( t == null ) {
                continue;
            }
            sectionList.add(t);
        }
        return sectionList;
    }

    public Boolean getRenderCitation() {
        return renderCitation;
    }

    public void setRenderCitation(Boolean renderCitation) {
        this.renderCitation = renderCitation;
    }
}
