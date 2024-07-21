package com.railway.utility;

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
import java.nio.file.Files;

public class PDFCreator {
    private  static  final String root = "C:\\Users\\Srila\\eclipse-workspace\\railwayProject\\";
    public void createPdf(String ticketNumber){
        try {
            // Initialize FOP factory and configuration
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            // Setup output stream
            OutputStream out = new FileOutputStream(root+"data\\"+ticketNumber+".pdf");

            // Construct FOP with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new File(root+"src\\main\\resources\\template.xsl")));
            String filePath = root+"data\\"+ticketNumber+".xml";
            // Setup input for XSLT transformation
            StreamSource src = new StreamSource(new File(filePath));

            // Resulting SAX events must be piped through to FOP
            SAXResult res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            // Close output stream
            out.close();

            //System.out.println("PDF Created Successfully!");
            java.nio.file.Path xmlFilePath = java.nio.file.Paths.get(filePath);
            Files.delete(xmlFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
