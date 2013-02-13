package cz.sio2.bibpub;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.http.HTTPClient;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

public class TestRunningRDFA {
    public static void main(String[] args) {
        try {
            /*1*/
            Any23 runner = new Any23();
/*2*/
            runner.setHTTPUserAgent("test-user-agent");
/*3*/
            HTTPClient httpClient = runner.getHTTPClient();
/*4*/
            DocumentSource source = null;
            try {
                source = new HTTPDocumentSource(
                        httpClient,
                        "http://localhost:8080/web/test/home"
                );
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
/*5*/
            ByteArrayOutputStream out = new ByteArrayOutputStream();
/*6*/
            TripleHandler handler = new NTriplesWriter(out);
            try {
/*7*/
                runner.extract(source, handler);
            } catch (ExtractionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
/*8*/
                try {
                    handler.close();
                } catch (TripleHandlerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
/*9*/
            String n3 = out.toString("UTF-8");


            System.out.println(n3);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
