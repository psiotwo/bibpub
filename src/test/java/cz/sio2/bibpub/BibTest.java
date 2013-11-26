package cz.sio2.bibpub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXString;
import org.jbibtex.ParseException;
import org.jbibtex.citation.ACSReferenceStyle;
import org.jbibtex.citation.ReferenceFormatter;
import org.jbibtex.citation.ReferenceStyle;
import org.junit.Assert;
import org.junit.Test;

public class BibTest {

    final String bibTeXEntry="@ARTICLE{kk2012eoqdev, author = {K{\\v r}emen, P. and Kostov, B.}, title = {{Expressive OWL Queries: Design, Evaluation, Visualization}}, journal = {International Journal on Semantic Web and Information Systems}, year = {2012}, volume = {8}, pages = {57--79}, number = {4}, issn = {1552-6283}, language = {English} }";
    
    private BibList list;
    
    {
	list = new BibList();
    }
    
    @Test
    public void testParseBib() {
	InputStream stream;
	try {
	    stream = new ByteArrayInputStream(bibTeXEntry.getBytes("UTF-8"));
	    final BibTeXDatabase db = BibList.parseBibTeX(stream);	
	    Assert.assertEquals(db.getObjects().size(), 1);
	    BibTeXEntry e = db.getEntries().values().iterator().next();
	    
	    ReferenceFormatter f = new RDFaReferenceFormatter();
	    System.out.println(f.format(e, true));
	    	    
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    Assert.fail();
	} catch (IOException e) {
	    e.printStackTrace();
	    Assert.fail();
	} catch (ParseException e) {
	    e.printStackTrace();
	    Assert.fail();
	}	
    }
    
}
