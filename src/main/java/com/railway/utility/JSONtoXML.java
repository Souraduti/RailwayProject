package com.railway.utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class JSONtoXML {
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

    public void getXML(JSONObject jsonObject,String fileName) {
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element element = convertObjectToXML(doc, jsonObject, "document");
            doc.appendChild(element);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            String root = "C:\\Users\\Srila\\eclipse-workspace\\railwayProject";
            StreamResult result = new StreamResult(new File(root +"\\data\\"+fileName));
            transformer.transform(source, result);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

