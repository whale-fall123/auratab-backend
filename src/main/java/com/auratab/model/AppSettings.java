package com.auratab.model;

/**
 * 应用设置模型，对应 settings.json
 */
public class AppSettings {
    private int schemaVersion = 1;
    private WebDAVConfig webdav;
    private RemoteConfig remote;
    private SyncConfig sync;

    // Getters and Setters
    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public WebDAVConfig getWebdav() {
        return webdav;
    }

    public void setWebdav(WebDAVConfig webdav) {
        this.webdav = webdav;
    }

    public RemoteConfig getRemote() {
        return remote;
    }

    public void setRemote(RemoteConfig remote) {
        this.remote = remote;
    }

    public SyncConfig getSync() {
        return sync;
    }

    public void setSync(SyncConfig sync) {
        this.sync = sync;
    }
}