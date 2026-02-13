package com.auratab.model;

/**
 * 远程存储配置
 */
public class RemoteConfig {
    private String path = "/qinghome/bookmarks.xbel";
    private String format = "xbel";

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}