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

public class JSONtoXML2 {
    Element convertArrayTOXML(Document doc, JSONArray arr, String key) {
        Element element = doc.createElement(key + "_details");
        if (arr != null && !arr.isEmpty()) {
            if (arr.get(0) instanceof JSONObject) {
                Element headerElement = doc.createElement("header");
                JSONObject jsonObject = (JSONObject) arr.get(0);
                for (String header : jsonObject.keySet()) {
                    Element value = doc.createElement(header);
                    value.appendChild(doc.createTextNode(Character.toUpperCase(header.charAt(0)) + header.substring(1).replace("_", " ").toUpperCase()));
                    headerElement.appendChild(value);
                }
                element.appendChild(headerElement);
            }
            for (int i = 0; i < arr.length(); i++) {
                Element childElement;
                Object childElementValue = arr.get(i);
                if (childElementValue instanceof JSONObject) {
                    childElement = convertObjectToXML(doc, arr.getJSONObject(i), key);
                    System.out.println("i = " + i);
                } else {
                    childElement = doc.createElement("details");
                    childElement.appendChild(doc.createTextNode(childElementValue.toString()));
                }
                element.appendChild(childElement);
            }
        }
        return element;
    }

    Element convertObjectToXML(Document doc, JSONObject obj, String key) {
        Element element = doc.createElement(key);
        for (String objectKey : obj.keySet()) {
            Element childElement;
            if (obj.get(objectKey) instanceof JSONObject) {
                childElement = convertObjectToXML(doc, obj.getJSONObject(objectKey), objectKey);
            } else if (obj.get(objectKey) instanceof JSONArray) {
                childElement = convertArrayTOXML(doc, obj.getJSONArray(objectKey), objectKey);
            } else {
                childElement = doc.createElement(objectKey);
                childElement.appendChild(doc.createTextNode(obj.get(objectKey).toString()));
            }
            element.appendChild(childElement);
        }
        return element;

    }

    @Test
    public void test() throws ParserConfigurationException, TransformerException {
        JSONObject jsonObject = new JSONObject("""
                {
                    "train_trip_details": {
                        "arrival": "11:56:00",
                        "from": "RNC",
                        "departure": "22:25:00",
                        "to": "PNC"
                    },
                    "passenger": [
                        {
                            "sl_no": "1",
                            "name": "Gautam Jalui",
                            "seat_no": 0,
                            "status": "CONFIRM"
                        },
                        {
                            "sl_no": "2",
                            "name": "Nilkamal Barman",
                            "seat_no": -3,
                            "status": "CANCELLED"
                        }
                    ],
                    "ticket_no": "150712037811",
                    "train_number": 18622,
                    "train_name": "HATIA PATNA EXPRESS",
                    "departure_date": "2024-08-03",
                    "message": "success"
                }""");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element element = convertObjectToXML(doc, jsonObject, "document");
        doc.appendChild(element);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("data/output3.xml"));
        transformer.transform(source, result);
    }
}
