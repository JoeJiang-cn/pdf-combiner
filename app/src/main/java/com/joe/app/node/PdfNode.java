package com.joe.app.node;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.property.TabAlignment;

/**
 * @author Joe
 * TODO description
 * 2021/6/22 21:42
 */
public class PdfNode extends FileNode{
    public PdfNode(String filename, int pageNum, int row) {
        super(filename, pageNum, row);
    }

    @Override
    public void print() {
        System.out.println("Pdf: " + pageNum + " " + filename);
    }

    @Override
    public void addOutLine(PdfDocument pdf, PdfOutline outline) {
        PdfPage pdfPage = pdf.getPage(pageNum);
        outline = outline.addOutline(filename);
        outline.addDestination(PdfExplicitDestination.createFitH(pdfPage, pdfPage.getPageSize().getTop()));
    }

    @Override
    public TwoTuple<Integer, Float> addToC(Document document, int totalTocPage, int page, float x, float y, float width, int position) {
        PdfPage pdfPage = document.getPdfDocument().getPage(pageNum + totalTocPage);
        Paragraph p = new Paragraph()
                .setFontFamily(StandardFonts.HELVETICA, "simsun")
                .addTabStops(new TabStop(position, TabAlignment.LEFT, new DottedLine()))
                .add(filename) // filename
                .add(new Tab())
                .add(String.valueOf(pageNum + totalTocPage)) // pageNum
                .setAction(PdfAction.createGoTo(PdfExplicitDestination.createFitH(pdfPage, pdfPage.getPageSize().getTop()))); // pageNum
        document.add(p
                .setFixedPosition(page, x, y, width)
                .setMargin(0)
                .setMultipliedLeading(1));
        return new TwoTuple<>(page, y);
    }
}
