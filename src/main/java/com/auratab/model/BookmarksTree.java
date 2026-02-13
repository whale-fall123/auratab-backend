package com.auratab.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * 书签树的根节点结构
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookmarksTree {
    private int schemaVersion = 1;
    private String updatedAt;
    private BookmarkNode root;

    public BookmarksTree() {
        this.updatedAt = Instant.now().toString();
    }

    // Getters and Setters
    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BookmarkNode getRoot() {
        return root;
    }

    public void setRoot(BookmarkNode root) {
        this.root = root;
    }
}