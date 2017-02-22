package com.xptschool.parent.common;

/**
 * Created by Administrator on 2016/10/27.
 */

public class LocalFile {

    private String originalUri;//原图URI
    private String thumbnailUri;//缩略图URI
    private int orientation;//图片旋转角度
    private String parentFileName;
    private String originalPath;//

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String path) {
        this.originalPath = path;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int exifOrientation) {
        orientation = exifOrientation;
    }

    public String getParentFileName() {
        return parentFileName;
    }

    public void setParentFileName(String parentFileName) {
        this.parentFileName = parentFileName;
    }
}
