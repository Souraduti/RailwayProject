package com.railway.servlet;

import com.railway.utility.JSONtoXML;
import com.railway.utility.PDFCreator;

import com.railway.utility.Utility;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/download")
public class PdfServlet extends HttpServlet {
    private JSONObject jsonSchema;
    private final String root = "C:\\Users\\Srila\\eclipse-workspace\\railwayProject\\";
    @Override
    public void init() throws ServletException {
        super.init();
        String schemaFilePath = root+"src\\main\\resources\\schema.json";
        try {
            String schemaContent = new String(Files.readAllBytes(Paths.get(schemaFilePath)));
            jsonSchema = new JSONObject(new JSONTokener(schemaContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean validateJson(JSONObject requestBody){
        try {
            // This will throw a ValidationException if the JSON is invalid
            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(requestBody);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @GET
    @Produces("application/pdf")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //request to Json
           JSONObject requestBody = Utility.formatRequestBody(request);
            if (requestBody == null || !validateJson(requestBody)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid request object");
                return;
            }
            String ticketNumber = requestBody.getString("ticket_number");
            requestBody.put("title", "Indian railways");

            JSONtoXML converter = new JSONtoXML();
            converter.getXML(requestBody,ticketNumber+".xml");

            PDFCreator pdfCreator = new PDFCreator();
            pdfCreator.createPdf(ticketNumber);

            // Path to the PDF file on the server
            String filePath = root+"data\\" + ticketNumber + ".pdf";
            //System.out.println("filePath = " + filePath);
            File downloadFile = new File(filePath);

            // Check if the file exists
            if (!downloadFile.exists()) {
                System.out.println("PDf File not found");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }

            // Set the response headers
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
            response.setContentLength((int) downloadFile.length());

            // Stream the file to the client
            try (FileInputStream inStream = new FileInputStream(downloadFile);
                 OutputStream outStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                    //System.out.println("bytesRead = " + bytesRead);
                }
                java.nio.file.Path pdfFilePath = java.nio.file.Paths.get(filePath);
                Files.delete(pdfFilePath);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


