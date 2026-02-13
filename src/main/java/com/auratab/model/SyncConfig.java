package com.auratab.model;

/**
 * 同步配置
 */
public class SyncConfig {
    private boolean preferRemoteOnConflict = true;

    // Getters and Setters
    public boolean isPreferRemoteOnConflict() {
        return preferRemoteOnConflict;
    }

    public void setPreferRemoteOnConflict(boolean preferRemoteOnConflict) {
        this.preferRemoteOnConflict = preferRemoteOnConflict;
    }
}