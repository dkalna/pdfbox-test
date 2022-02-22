import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PdfboxTest {

    public static void main(String[] args) throws Exception {
        PdfboxTest pdfboxTest = new PdfboxTest();
        pdfboxTest.rotate();
        Thread.sleep(1000);
        pdfboxTest.print();
    }

    private void rotate() throws Exception {

        PDDocument pdDocument = PDDocument.load(new File("simple.pdf"));
        for(int pageIndex=0; pageIndex<pdDocument.getNumberOfPages(); pageIndex++) {

            PDPage pdPage = pdDocument.getPage(pageIndex);
            PDRectangle mediaBox = pdPage.getMediaBox();

            if(mediaBox.getWidth() > mediaBox.getHeight()) {
                PDPageContentStream contents = new PDPageContentStream(
                        pdDocument, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);

                float width = pdPage.getMediaBox().getHeight();
                float height = pdPage.getMediaBox().getWidth();
                contents.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));

                pdPage.setRotation(270);

                contents.close();
            }
        }

        pdDocument.save(new File("simple_rotated.pdf"));
        pdDocument.close();
    }

    private void print() throws Exception {
        PDDocument pdDocument = PDDocument.load(new File("simple_rotated.pdf"));

        for(int pageIndex=0; pageIndex<pdDocument.getNumberOfPages(); pageIndex++) {

            PDPage pdPage = pdDocument.getPage(pageIndex);
            PDPageContentStream contents = new PDPageContentStream(
                    pdDocument, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);

            applyPrintableInfoToPage(pdPage, contents);
            contents.close();
        }

        pdDocument.save(new File("simple_rotated_markers.pdf"));
        pdDocument.close();
    }

    private void applyPrintableInfoToPage(PDPage pdPage, PDPageContentStream contentStream) throws IOException {

        contentStream.setFont(PDType1Font.HELVETICA, (float) 6);
        contentStream.beginText();
        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, 20, pdPage.getMediaBox().getHeight() / 2 - 25));
        contentStream.showText("TestApp" + "   " + UUID.randomUUID() + " 1 / 1");
        contentStream.endText();
    }
}
