import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.util.UUID;

public class PdfboxTest {

    public static void main(String[] args) throws Exception {
        PdfboxTest pdfboxTest = new PdfboxTest();
        pdfboxTest.rotate();
        Thread.sleep(1000);
        pdfboxTest.print("TestApp" + "   " + UUID.randomUUID() + " 1 / 1");
    }

    private void rotate() throws Exception {

        PDDocument pdDocument = PDDocument.load(new File("simple.pdf"));
        for(int pageIndex=0; pageIndex<pdDocument.getNumberOfPages(); pageIndex++) {

            PDPage pdPage = pdDocument.getPage(pageIndex);
            PDRectangle mediaBox = pdPage.getMediaBox();

            if(mediaBox.getWidth() > mediaBox.getHeight()) {
                pdPage.setRotation(270);
            }
        }

        pdDocument.save(new File("simple_rotated.pdf"));
        pdDocument.close();
    }

    private void print(String message) throws Exception {
        PDDocument pdDocument = PDDocument.load(new File("simple_rotated.pdf"));

        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 6.0f;

        for( PDPage pdPage : pdDocument.getPages()) {

            PDRectangle pageSize = pdPage.getMediaBox();
            float stringWidth = font.getStringWidth( message )*fontSize/1000f;

            // calculate to center of the page
            int rotation = pdPage.getRotation();
            boolean rotate = rotation == 90 || rotation == 270;

            float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
            float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
            float centerX = rotate ? pageHeight/2f : (pageWidth - stringWidth)/2f;
            float centerY = rotate ? (pageWidth - stringWidth)/2f : pageHeight/2f;

            // append the content to the existing stream
            try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage, PDPageContentStream.AppendMode.APPEND, true, true))
            {
                contentStream.beginText();
                // set font and font size
                contentStream.setFont( font, fontSize );
                if (rotate) {
                    // rotate the text according to the page rotation
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, pageWidth - 20));
                } else {
                    contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, 20, centerY));
                }
                contentStream.showText(message);
                contentStream.endText();
            }
        }

        pdDocument.save(new File("simple_rotated_markers.pdf"));
        pdDocument.close();
    }
}