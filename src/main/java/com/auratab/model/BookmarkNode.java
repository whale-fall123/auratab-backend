package com.auratab.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 书签节点，可以是文件夹或链接
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookmarkNode {
    private String type; // "folder" or "link"
    private String id;
    private String title;
    private String url; // 仅对链接类型有效
    private List<BookmarkNode> children; // 仅对文件夹类型有效

    // Default constructor for Jackson
    public BookmarkNode() {}

    public BookmarkNode(String type, String id, String title) {
        this.type = type;
        this.id = id;
        this.title = title;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<BookmarkNode> getChildren() {
        return children;
    }

    public void setChildren(List<BookmarkNode> children) {
        this.children = children;
    }
}