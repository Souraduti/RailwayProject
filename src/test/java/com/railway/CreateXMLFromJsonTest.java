package com.railway;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class CreateXMLFromJsonTest {
    @Test
    void test() throws ParserConfigurationException, TransformerException {
        String jsonString = """
                {
                    "passengers": [
                        {
                            "sl_no": "1",
                            "name": "Gautam Jalui",
                            "seat_no": "1",
                            "status": "CONFIRM"
                        },
                        {
                            "sl_no": "2",
                            "name": "Nilkamal Barman",
                            "seat_no": "2",
                            "status": "CONFIRM"
                        }
                    ],
                    "train_number": 18622,
                    "train_name": "HATIA PATNA EXPRESS",
                    "message": "success"
                }
                """;
        JSONObject jsonObject = new JSONObject(jsonString);
        File xmlFile = new File("data/data.xml");
        // Create a new XML document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Convert JSON to XML
        Element rootElement = doc.createElement("Document");
        doc.appendChild(rootElement);

        Element childElement = doc.createElement("passenger");
        childElement.appendChild(doc.createTextNode("hello"));
        rootElement.appendChild(childElement);
        // Write the XML document to a file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);


    }

    private void appendPassengerDetails(JSONArray passengerDetails){}
}
