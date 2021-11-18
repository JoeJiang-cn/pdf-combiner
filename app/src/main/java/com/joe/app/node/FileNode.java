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
    protected int pageNum; // 合并后的起始页

    public FileNode(String filename, int pageNum) {
        this.filename = filename;
        this.pageNum = pageNum;
    }

    public FileNode add(FileNode node) {
        throw new UnsupportedOperationException();
    }

    public List<FileNode> getChildren() {
        throw new UnsupportedOperationException();
    }

    public abstract void print();

    /**
     * 增加大纲
     * @param pdf
     * @param outline
     */
    public abstract void addOutLine(PdfDocument pdf, PdfOutline outline);

    /**
     * 增加目录
     * @param document 文档
     * @param page 第几页
     * @param x 目录条目起始的横坐标
     * @param y 目录条目起始的纵坐标
     * @param width 目录条目宽度
     * @param position
     * @return
     */
    public abstract TwoTuple<Integer, Float> addToC(Document document, int totalTocPage, int page, float x, float y, float width, int position);
}
