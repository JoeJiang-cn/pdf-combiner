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
import com.joe.app.PdfOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joe
 * TODO description
 * 2021/6/22 21:43
 */
public class DirNode extends FileNode{
    private List<FileNode> children = new ArrayList<>();

    public DirNode(String filename, String path, int pageNum, int row) {
        super(filename, path, pageNum, row);
    }

    @Override
    public FileNode add(FileNode node) {
        children.add(node);
        return node;
    }

    @Override
    public List<FileNode> getChildren() {
        return children;
    }

    @Override
    public void print() {
        System.out.println("Dir: " + pageNum + " " + filename);
        for (FileNode node : children) {
            node.print();
        }
    }

    @Override
    public void addOutLine(PdfDocument pdf, PdfOutline outline) {
        PdfPage pdfPage = pdf.getPage(pageNum);
        outline = outline.addOutline(filename);
        outline.addDestination(PdfExplicitDestination.createFitH(pdfPage, pdfPage.getPageSize().getTop()));
        for (FileNode node : children) {
            node.addOutLine(pdf, outline);
        }
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
        TwoTuple<Integer, Float> pageAndY = new TwoTuple<>(page, y);
        for (FileNode node : children) {
            if (pageAndY.getSecond() <= 110) {
                // 换页
                pageAndY = node.addToC(document, totalTocPage, pageAndY.getFirst() + 1, x + 20, PdfOperator.tocYCoordinate, width, position - 20);
            } else {
                pageAndY = node.addToC(document, totalTocPage, pageAndY.getFirst(), x + 20, pageAndY.getSecond() - 20, width, position - 20);
            }
        }
        return pageAndY;
    }

}
