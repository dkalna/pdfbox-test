import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PdfboxTest {

    public static void main(String[] args) throws Exception {
        PdfboxTest pdfboxTest = new PdfboxTest();
        pdfboxTest.start();
    }

    private void start() throws Exception {
        PDDocument pdDocument = PDDocument.load(new File("simple.pdf"));

        for(int pageIndex=0; pageIndex<pdDocument.getNumberOfPages(); pageIndex++) {

            PDPage pdPage = pdDocument.getPage(pageIndex);
            PDPageContentStream contents = new PDPageContentStream(
                    pdDocument, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);

            PDRectangle mediaBox = pdPage.getMediaBox();
            if(mediaBox.getWidth() > mediaBox.getHeight()) {
                float width = pdPage.getMediaBox().getHeight();
                float height = pdPage.getMediaBox().getWidth();
                contents.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));

                pdPage.setRotation(270);
            }

            applyPrintableInfoToPage(pdPage, contents);
            addMarker(contents);
            addText(contents, "123456-789");
            addAplus(contents, "A+");
            addBarcode(pdDocument, contents, "123456-789");

            contents.close();
        }

        pdDocument.save(new File("simple_markers.pdf"));
        pdDocument.close();
    }

    // SVTI Code
    private void applyPrintableInfoToPage(PDPage pdPage, PDPageContentStream contentStream) throws IOException {

        contentStream.setFont(PDType1Font.HELVETICA, (float) 6);
        contentStream.beginText();
        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, 20, pdPage.getMediaBox().getHeight() / 2 - 25));
        contentStream.showText("TestApp" + "   " + UUID.randomUUID() + " 1 / 1");
        contentStream.endText();
    }

    private void addMarker(PDPageContentStream contentStream) throws IOException {

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 16);
        contentStream.newLineAtOffset(150, 831);
        contentStream.showText("| | || | |");
        contentStream.endText();
    }

    private void addText(PDPageContentStream contentStream, String number) throws IOException {

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(411, 722);
        contentStream.showText(number);
        contentStream.endText();
    }

    private void addAplus(PDPageContentStream contentStream, String number) throws IOException {

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.newLineAtOffset(320, 730);
        contentStream.showText(number);
        contentStream.endText();
    }

    private void addBarcode(PDDocument document, PDPageContentStream contents, String number)
            throws BarcodeException, OutputException, IOException {

        Barcode bc = BarcodeFactory.createCode128(number);
        bc.setDrawingText(false);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BarcodeImageHandler.writePNG(bc, os);
        PDImageXObject image = PDImageXObject.createFromByteArray(document, os.toByteArray(), null);
        contents.drawImage(image, 360, 730, 200, 18);
    }
}
