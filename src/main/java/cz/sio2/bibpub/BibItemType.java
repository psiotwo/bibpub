package cz.sio2.bibpub;

public enum BibItemType {

    ARTICLE("Article"), BOOK("Book"), INPROCEEDINGS("InProceedings"), OTHER("Other");

    String type;

    private BibItemType(String bibtexType) {
        this.type = bibtexType;
    }

    public String getType() {
        return type;
    }

    public static BibItemType forType(String type) {
        for ( BibItemType x : BibItemType.values() ) {
            if ( x.getType().equalsIgnoreCase(type)) {
                return x;
            }
        }
        return null;
    }
}
