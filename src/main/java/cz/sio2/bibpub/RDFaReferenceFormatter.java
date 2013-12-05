package cz.sio2.bibpub;

import org.jbibtex.BibTeXEntry;
import org.jbibtex.citation.ReferenceFormatter;

public class RDFaReferenceFormatter extends ReferenceFormatter {

    public RDFaReferenceFormatter() {
	super(new RDFaACSReferenceStyle());
    }

    @Override
    public String format(BibTeXEntry entry, boolean html) {
	if (!html) {
	    throw new IllegalArgumentException();
	}
	return super.format(entry, html);
    }

    /**
     * @param entry
     * @param latex
     *            Indicates if the <code>entry</code>'s field values should be
     *            regarded as LaTeX strings. Should be <code>true</code> when
     *            the entry was obtained via parsing a BibTeX file, should be
     *            <code>false</code> when the entry was constructed manually.
     * @param html
     */
    public String format(BibTeXEntry entry, boolean latex, boolean html) {
	if (!html) {
	    throw new IllegalArgumentException();
	}
	String s = super.format(entry, latex, html);
	s = "<p prefix=\"bib: http://zeitkunst.org/bibtex/0.2/bibtex.owl#\" typeof=\"bib:Entry\">"
		+ s + "</p>";
	return s;
    }

}
