package com.railway;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CreatePDFTest {
    public static void main(String[] args) {
        try {
            // Initialize FOP factory and configuration
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            // Setup output stream
            OutputStream out = new FileOutputStream("data/output4.pdf");

            // Construct FOP with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new File("src/test/resources/template.xsl")));

            // Setup input for XSLT transformation
//            StreamSource src = new StreamSource(new File("src/test/resources/data.xml"));
            StreamSource src = new StreamSource(new File("data/output3.xml"));

            // Resulting SAX events must be piped through to FOP
            SAXResult res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            // Close output stream
            out.close();

            System.out.println("PDF Created Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
