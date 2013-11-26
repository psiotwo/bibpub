package cz.sio2.bibpub;

import java.util.Arrays;
import java.util.List;

import org.jbibtex.BibTeXEntry;
import org.jbibtex.Value;
import org.jbibtex.citation.EntryFormat;
import org.jbibtex.citation.FieldFormat;
import org.jbibtex.citation.ReferenceStyle;

public class RDFaACSReferenceStyle extends ReferenceStyle {

    	public RDFaACSReferenceStyle(){
		addFormat(BibTeXEntry.TYPE_ARTICLE, createArticleFormat());
		addFormat(BibTeXEntry.TYPE_BOOK, createBookFormat());
		addFormat(BibTeXEntry.TYPE_INCOLLECTION, createInCollectionFormat());
		addFormat(BibTeXEntry.TYPE_INPROCEEDINGS, createInProceedingsFormat());
		addFormat(BibTeXEntry.TYPE_PHDTHESIS, createPhDFormat());
		addFormat(BibTeXEntry.TYPE_MISC, createMiscFormat());
		addFormat(BibTeXEntry.TYPE_INBOOK, createInBookFormat());
		addFormat(BibTeXEntry.TYPE_TECHREPORT, createTechReportFormat());
		addFormat(BibTeXEntry.TYPE_UNPUBLISHED, createUnpublishedFormat());
	}

	private static String rdfa(String input, String property) {
	    return "<span property=\""+property+"\">"+input+"</span>";
	}
	
	static
	private EntryFormat createArticleFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new JournalFormat(null),
			new FieldFormat(BibTeXEntry.KEY_YEAR, ","),
			new VolumeFormat(","),
			new FieldFormat(BibTeXEntry.KEY_NUMBER, ","),
			new FieldFormat(BibTeXEntry.KEY_PAGES, "."),
			new DOIFormat(null)
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createBookFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new BookTitleFormat(";"),
			new EditorFormat(";"),
			new FieldFormat(BibTeXEntry.KEY_PUBLISHER, ":"),
			new FieldFormat(BibTeXEntry.KEY_ADDRESS, ";"),
			new FieldFormat(BibTeXEntry.KEY_YEAR, "."),
			new DOIFormat(null)
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createInCollectionFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new InBookTitleFormat(";"),
			new EditorFormat(";"),
			new FieldFormat(BibTeXEntry.KEY_PUBLISHER, ";"),
			new FieldFormat(BibTeXEntry.KEY_YEAR, "."),
			new DOIFormat(null)
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createInProceedingsFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new InBookTitleFormat(";"),
			new EditorFormat(";"),
			new FieldFormat(BibTeXEntry.KEY_ORGANIZATION, ";"),
			new FieldFormat(BibTeXEntry.KEY_YEAR, "."),
			new DOIFormat(null)
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createPhDFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new FieldFormat(BibTeXEntry.KEY_ORGANIZATION, ","),
			new FieldFormat(BibTeXEntry.KEY_YEAR, ",")			
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createTechReportFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new FieldFormat(BibTeXEntry.KEY_ORGANIZATION, ","),
			new FieldFormat(BibTeXEntry.KEY_YEAR, ",")			
		);

		return new EntryFormat(fields);
	}


	static
	private EntryFormat createInBookFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new InBookTitleFormat(";"),
			new EditorFormat(";"),
			new FieldFormat(BibTeXEntry.KEY_ORGANIZATION, ";"),
			new FieldFormat(BibTeXEntry.KEY_YEAR, "."),
			new DOIFormat(null)
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createMiscFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, "."),
			new FieldFormat(BibTeXEntry.KEY_YEAR, ",")
		);

		return new EntryFormat(fields);
	}

	static
	private EntryFormat createUnpublishedFormat(){
		List<FieldFormat> fields = Arrays.asList(
			new AuthorFormat(null),
			new FieldFormat(BibTeXEntry.KEY_TITLE, ".")
		);

		return new EntryFormat(fields);
	}

	static
	private String bold(String string, boolean html){

		if(html){
			string = ("<b>" + string + "</b>");
		}

		return string;
	}

	static
	private String italic(String string, boolean html){

		if(html){
			string = ("<i>" + string + "</i>");
		}

		return string;
	}

	static
	private class AuthorFormat extends FieldFormat {

		public AuthorFormat(String separator){
			super(BibTeXEntry.KEY_AUTHOR, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			string = string.replace(" and ", "; ");

			return rdfa(string,"bib:hasAuthor");
		}
	}

	static
	private class BookTitleFormat extends FieldFormat {

		public BookTitleFormat(String separator){
			super(BibTeXEntry.KEY_BOOKTITLE, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			return italic(rdfa(string,"bib:hasBooktitle"), html);
		}
	}

	static
	private class DOIFormat extends FieldFormat {

		public DOIFormat(String separator){
			super(BibTeXEntry.KEY_DOI, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			if(html){
				string = ("DOI: <a href=\"http://dx.doi.org/" + string + "\">" + string + "</a>");
			} else

			{
				string = ("DOI: " + string);
			}

			return string;
		}
	}

	static
	private class EditorFormat extends FieldFormat {

		public EditorFormat(String separator){
			super(BibTeXEntry.KEY_EDITOR, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			string = string.replace(" and ", "; ");

			boolean plural = string.contains("; ");

			string = (rdfa(string,"bib:hasEditor") + ", " + (plural ? "Eds." : "Ed."));

			return string;
		}
	}

	static
	private class InBookTitleFormat extends BookTitleFormat {

		public InBookTitleFormat(String separator){
			super(separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);
			string = ("In " + rdfa(string,"hasBooktitle"));
			return string;
		}
	}

	static
	private class JournalFormat extends FieldFormat {

		public JournalFormat(String separator){
			super(BibTeXEntry.KEY_JOURNAL, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			return rdfa(italic(string, html),"bib:hasJournal");
		}
	}

	static
	private class VolumeFormat extends FieldFormat {

		public VolumeFormat(String separator){
			super(BibTeXEntry.KEY_VOLUME, separator);
		}

		@Override
		public String format(Value value, boolean latex, boolean html){
			String string = super.format(value, latex, html);

			return rdfa(bold(string, html),"bib:hasVolume");
		}
	}
}
