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

public class PdfboxTest {

    public static void main(String[] args) throws Exception {
        PdfboxTest pdfboxTest = new PdfboxTest();
        pdfboxTest.start();
    }

    private void start() throws Exception {
        PDDocument document = PDDocument.load(new File("verfuegung.pdf"));

        for(int pageIndex=0; pageIndex<document.getNumberOfPages(); pageIndex++) {

            PDPage page = document.getPage(pageIndex);
            PDPageContentStream contents = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            if(pageIndex==0) {
                addBarcode(document, contents, "991220123456123456");
                addAplus( contents, "A+");
                addText(contents, "99.12.201234.56123456");
            }
            PDRectangle mediaBox = page.getMediaBox();
            if(mediaBox.getWidth() > mediaBox.getHeight()) {
                float width = page.getMediaBox().getHeight();
                float height = page.getMediaBox().getWidth();
                contents.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));

                page.setRotation(270);
            }
            addMarker(contents);

            contents.close();
        }

        document.save(new File("test.pdf"));
        document.close();
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
        contentStream.newLineAtOffset(333, 730);
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
