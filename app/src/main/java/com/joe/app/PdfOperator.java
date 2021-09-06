package com.joe.app;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import com.joe.app.node.DirNode;
import com.joe.app.node.FileNode;
import com.joe.app.node.PdfNode;
import com.joe.app.node.TwoTuple;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

/**
 * @author Joe
 * 使用了组合的设计模式，处理文件树
 * 2021/6/22 13:58
 */
public class PdfOperator {
    public static final float tocYCoordinate = 770;
    private PdfDocument pdf;
    private FileNode root;
    private int rows;

    public PdfOperator(String destPath) {
        pdf = createPdf(destPath);
    }

    private PdfDocument createPdf(String path) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try {
            return new PdfDocument(new PdfWriter(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startTraverse(String path) {
        File file = new File(path);
        root = new DirNode(file.getName(), path, 1, rows); // 需要从1开始
        try {
            traverseFolder(file, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历生成树节点，并执行merge操作
     * @param file
     * @param node
     * @throws Exception
     */
    private void traverseFolder(File file, FileNode node) throws Exception {
        if (file.exists() || !file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    FileNode child = node.add(new DirNode(subFile.getName(), subFile.getAbsolutePath(),
                            pdf.getNumberOfPages() + 1, ++rows));
                    // 遍历子节点
                    traverseFolder(subFile, child);
                } else {
                    if (isPDF(subFile)) {
                        // merge
                        PdfMerger merger = new PdfMerger(pdf);
                        // Add pages from the first document
                        PdfDocument pdfToMerge = new PdfDocument(new PdfReader(subFile));
                        int numOfPages = pdfToMerge.getNumberOfPages();
                        merger.merge(pdfToMerge, 1, numOfPages);
                        String title = getFirstLine(pdfToMerge);
                        pdfToMerge.close();
                        node.add(new PdfNode(title, subFile.getAbsolutePath(),
                                pdf.getNumberOfPages() + 1 - numOfPages, ++rows));
                    }
                }
            }
        } else {
            throw new Exception("请输入正确的文件夹地址！");
        }
    }

    /**
     * 获得pdf文档的第一行有内容的文本
     * @param pdfDocument
     * @return
     */
    private String getFirstLine(PdfDocument pdfDocument) {
        String pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getFirstPage(), new SimpleTextExtractionStrategy());
        String[] textArray = pageText.split("\n");
        for (String text : textArray) {
            if (text != null && text.trim().length() > 0) {
                return text;
            }
        }
        return null;
    }

    public void print() {
        root.print();
    }

    /**
     * 添加大纲
     */
    public void addOutline() {
        for (FileNode node : root.getChildren()) {
            node.addOutLine(pdf, pdf.getOutlines(false));
        }
    }

    /**
     * 添加目录页
     */
    public void addTableOfContents() {
        Document document = new Document(pdf);
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.HELVETICA_BOLD);
        provider.getFontSet().addFont(StandardFonts.HELVETICA);
        provider.getFontSet().addFont("/com/joe/app/font/simsun.ttf", PdfEncodings.IDENTITY_H);
        document.setFontProvider(provider);

        float tocXCoordinate = document.getLeftMargin();
        float tocWidth = pdf.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();

        // 目录页总数，先把这个求出来
        int totalTocPage = 0;
        // 770~110为1页，(770 - 110) / 20 = 33
        for (int i = 0; i < rows / 33 + 1; i++) {
            totalTocPage++;
            pdf.addNewPage(i + 1); // 从1开始
        }

        // 目录标题段
        Paragraph p = new Paragraph("Table of Contents")
                .setFontFamily(StandardFonts.HELVETICA_BOLD)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(p.setFixedPosition(tocXCoordinate, tocYCoordinate, tocWidth)
                .setMargin(0)
                .setMultipliedLeading(1));

        int tabPosition = 500;

        // <页数，Y坐标>
        TwoTuple<Integer, Float> pageAndY = new TwoTuple<>(1, tocYCoordinate);

        // 遍历+递归生成目录树
        for (FileNode node : root.getChildren()) {
            if (pageAndY.getSecond() <= 110) {
                pageAndY = node.addToC(document, totalTocPage, pageAndY.getFirst() + 1, tocXCoordinate, tocYCoordinate, tocWidth, tabPosition);
            } else {
                pageAndY = node.addToC(document, totalTocPage, pageAndY.getFirst(), tocXCoordinate, pageAndY.getSecond() - 20, tocWidth, tabPosition);
            }
        }
    }

    private boolean isPDF(File file) throws Exception{
        Scanner input = new Scanner(new FileReader(file));
        while (input.hasNextLine()) {
            final String checkLine = input.nextLine();
            if (checkLine.contains("%PDF-")) {
                // a match!
                return true;
            }
        }
        return false;
    }

    public void close() {
        if (pdf != null) {
            pdf.close();
        }
    }
}
