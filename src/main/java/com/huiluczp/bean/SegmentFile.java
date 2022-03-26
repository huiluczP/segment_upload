package com.huiluczp.bean;

import java.io.Serializable;

public class SegmentFile implements Serializable {
    private static final long serialVersionUID = -1937877331354085546L;

    private int id;
    private String filePath;
    private String fileName;
    private int size;
    private int segmentIndex;
    private int segmentSize;
    private int segmentTotal;
    private String md5Key;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public int getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(int segmentSize) {
        this.segmentSize = segmentSize;
    }

    public int getSegmentTotal() {
        return segmentTotal;
    }

    public void setSegmentTotal(int segmentTotal) {
        this.segmentTotal = segmentTotal;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }
}
