package com.auratab.service;

import com.auratab.model.AppSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SettingsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private static final String SETTINGS_DIR = "./data";
    private static final String SETTINGS_FILE = SETTINGS_DIR + "/settings.json";
    
    private final ObjectMapper objectMapper;
    private AppSettings appSettings;
    
    public SettingsService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 格式化输出
        this.appSettings = new AppSettings(); // 初始化默认设置
    }
    
    @PostConstruct
    public void initialize() {
        loadSettings();
    }
    
    /**
     * 加载设置文件
     */
    public AppSettings loadSettings() {
        Path settingsPath = Paths.get(SETTINGS_FILE);
        
        // 如果文件不存在，创建默认设置
        if (!Files.exists(settingsPath)) {
            logger.info("Settings file does not exist, creating default settings at: {}", settingsPath.toAbsolutePath());
            ensureDataDirectoryExists();
            
            // 创建默认设置
            appSettings = new AppSettings();
            saveSettings(appSettings);
            return appSettings;
        }
        
        try {
            appSettings = objectMapper.readValue(settingsPath.toFile(), AppSettings.class);
            logger.debug("Settings loaded from: {}", settingsPath.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to load settings from: {}", settingsPath.toAbsolutePath(), e);
            // 如果加载失败，返回默认设置
            appSettings = new AppSettings();
        }
        
        return appSettings;
    }
    
    /**
     * 保存设置到文件
     */
    public boolean saveSettings(AppSettings settings) {
        Path settingsPath = Paths.get(SETTINGS_FILE);
        
        try {
            ensureDataDirectoryExists();
            
            // 更新内部状态
            this.appSettings = settings;
            
            // 写入文件
            objectMapper.writeValue(settingsPath.toFile(), settings);
            logger.debug("Settings saved to: {}", settingsPath.toAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Failed to save settings to: {}", settingsPath.toAbsolutePath(), e);
            return false;
        }
    }
    
    /**
     * 获取当前设置
     */
    public AppSettings getCurrentSettings() {
        return appSettings != null ? appSettings : loadSettings();
    }
    
    /**
     * 确保数据目录存在
     */
    private void ensureDataDirectoryExists() {
        try {
            Path dataDir = Paths.get(SETTINGS_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                logger.info("Created settings directory: {}", dataDir.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to create settings directory", e);
        }
    }
}