package com.railway.servlet;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Path("/p")
public class PdfServlet extends HttpServlet {
    @GET
    @Produces("application/pdf")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getPdf(req, resp);
    }


    public void getPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("hi");
        try {
            // Load the PDF template
            PdfReader reader = new PdfReader("C://Users/Srila/eclipse-workspace/railwayProject/src/main/resources/ticketTemplate.pdf");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);

            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

            // Fill in the form fields
            Map<String, PdfFormField> fields = form.getFormFields();
            fields.put("TrainNumber", new PdfFormField(new )).setValue("12308");
            fields.get("TrainName").setValue("Rajdhani Express");
            fields.get("From").setValue("HWH");
            fields.get("To").setValue("DLI");
            fields.get("Date").setValue("12 Aug 2024");
            fields.get("TicketNumber").setValue("1245367847745");

            // Assume there are fields for passenger details
            fields.get("SlNo1").setValue("1");
            fields.get("Name1").setValue("John Doe");
            fields.get("Reservation1").setValue("Confirmed");
            fields.get("SeatNo1").setValue("A1");

            // Flatten the form to make it uneditable
            form.flattenFields();

            // Close the document
            pdfDoc.close();

            // Convert the byte array output stream to a byte array
            byte[] pdfData = byteArrayOutputStream.toByteArray();

            // Set the response content type and headers
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"ticket.pdf\"");
            resp.setContentLength(pdfData.length);

            // Write the PDF data to the response output stream
            resp.getOutputStream().write(pdfData);

            // Flush the output stream
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
    }
}

