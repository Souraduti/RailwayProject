package com.railway;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JSONtoPDFTEst {
        public static void main(String[] args) {
            try {
                // Read JSON from a file
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(new File("data/temp.json"));

                // Create a PDF document
                PdfWriter writer = new PdfWriter("data/output2.pdf");
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                // Set font
                document.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));

                // Convert JSON to PDF
                processJsonNode(jsonNode, document, "");

                // Close the document
                document.close();

                System.out.println("PDF file created successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void processJsonNode(JsonNode jsonNode, Document document, String indent) {
            if (jsonNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    document.add(new Paragraph(indent + field.getKey() + ":"));
                    processJsonNode(field.getValue(), document, indent + "  ");
                }
            } else if (jsonNode.isArray()) {
                for (JsonNode arrayItem : jsonNode) {
                    processJsonNode(arrayItem, document, indent + "  ");
                }
            } else {
                document.add(new Paragraph(indent + jsonNode.asText()));
            }
        }


}
