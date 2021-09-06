package com.joe.app.node;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.layout.Document;

import java.util.List;

/**
 * @author Joe
 * TODO description
 * 2021/6/22 18:34
 */
public abstract class FileNode {
    protected String filename;
    protected String path;
    protected int pageNum; // 合并后的起始页
    protected int row; // 表示位于目录的第几行

    public FileNode(String filename, String path, int pageNum, int row) {
        this.filename = filename;
        this.path = path;
        this.pageNum = pageNum;
        this.row = row;
    }

    public abstract FileNode add(FileNode node);

    public abstract List<FileNode> getChildren();

    public abstract void print();

    /**
     * 增加大纲
     * @param pdf
     * @param outline
     */
    public abstract void addOutLine(PdfDocument pdf, PdfOutline outline);

    /**
     * 增加目录
     * @param document
     * @param page 第几页
     * @param x
     * @param y
     * @param width
     * @param position
     * @return
     */
    public abstract TwoTuple<Integer, Float> addToC(Document document, int totalTocPage, int page, float x, float y, float width, int position);
}
