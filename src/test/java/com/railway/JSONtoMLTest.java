package com.railway;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONtoMLTest {
    public static void main(String[] args) {
        try {
            // Read JSON from a file
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

            // Convert JSON to XML
            String xml = XML.toString(jsonObject);

            // Write XML to a file
            try (FileWriter fileWriter = new FileWriter("data/output1.xml")) {
                fileWriter.write(xml);
            }

            System.out.println("XML file created successfully!");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
