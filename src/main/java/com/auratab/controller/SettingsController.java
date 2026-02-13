package com.auratab.controller;

import com.auratab.model.AppSettings;
import com.auratab.model.BookmarksTree;
import com.auratab.service.SettingsService;
import com.auratab.service.WebDAVService;
import com.auratab.service.XBELParserService;
import com.auratab.service.XBELExporterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SettingsController {
    
    private final SettingsService settingsService;
    private final WebDAVService webdavService;
    private final XBELParserService xbelParserService;
    private final XBELExporterService xbelExporterService;
    
    public SettingsController(SettingsService settingsService, WebDAVService webdavService, 
                             XBELParserService xbelParserService, XBELExporterService xbelExporterService) {
        this.settingsService = settingsService;
        this.webdavService = webdavService;
        this.xbelParserService = xbelParserService;
        this.xbelExporterService = xbelExporterService;
    }
    
    /**
     * 获取当前设置
     */
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        try {
            AppSettings settings = settingsService.getCurrentSettings();
            return ResponseEntity.ok().body(new ApiResponse(true, settings));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResponse(false, null, e.getMessage()));
        }
    }
    
    /**
     * 保存设置
     */
    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody AppSettings settings) {
        try {
            boolean success = settingsService.saveSettings(settings);
            if (success) {
                return ResponseEntity.ok().body(new ApiResponse(true, settings));
            } else {
                return ResponseEntity.ok().body(new ApiResponse(false, null, "Failed to save settings"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResponse(false, null, e.getMessage()));
        }
    }
    
    /**
     * 测试WebDAV连接
     */
    @PostMapping("/webdav/test")
    public ResponseEntity<?> testWebDAVConnection(@RequestBody AppSettings settings) {
        try {
            Map<String, Object> result = webdavService.testConnection(settings.getWebdav());
            return ResponseEntity.ok().body(new ApiResponse(true, result));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResponse(false, null, e.getMessage()));
        }
    }
    
    /**
     * 从WebDAV拉取书签
     */
    @GetMapping("/bookmarks/pull")
    public ResponseEntity<?> pullBookmarks() {
        try {
            AppSettings settings = settingsService.getCurrentSettings();
            String remotePath = settings.getRemote().getPath();
            
            // 从WebDAV下载XBEL文件
            byte[] xbelContent = webdavService.downloadFile(settings.getWebdav(), remotePath);
            
            // 解析XBEL内容为BookmarksTree
            BookmarksTree tree = xbelParserService.parseXBEL(xbelContent);
            
            // 创建响应数据，包含树结构和远程指纹信息
            Map<String, Object> responseData = Map.of(
                "tree", tree
                // 注意：这里可以添加远程指纹信息（如ETag或Last-Modified），但需要根据实际实现来确定
            );
            
            return ResponseEntity.ok().body(new ApiResponse(true, responseData));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResponse(false, null, "Failed to pull bookmarks: " + e.getMessage()));
        }
    }
    
    /**
     * 推送书签到WebDAV
     */
    @PostMapping("/bookmarks/push")
    public ResponseEntity<?> pushBookmarks(@RequestBody BookmarksTree tree) {
        try {
            AppSettings settings = settingsService.getCurrentSettings();
            String remotePath = settings.getRemote().getPath();
            
            // 将BookmarksTree导出为XBEL格式
            byte[] xbelContent = xbelExporterService.exportToXBEL(tree);
            
            // 上传XBEL内容到WebDAV
            webdavService.uploadFile(settings.getWebdav(), remotePath, xbelContent);
            
            // 可以返回上传后的远程指纹信息
            Map<String, Object> responseData = Map.of(
                "message", "Bookmarks pushed successfully"
                // 注意：这里可以添加新的远程指纹信息
            );
            
            return ResponseEntity.ok().body(new ApiResponse(true, responseData));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResponse(false, null, "Failed to push bookmarks: " + e.getMessage()));
        }
    }
    
    /**
     * 通用响应类
     */
    public static class ApiResponse {
        public final boolean ok;
        public final Object data;
        public final String error;
        
        public ApiResponse(boolean ok, Object data) {
            this.ok = ok;
            this.data = data;
            this.error = null;
        }
        
        public ApiResponse(boolean ok, Object data, String error) {
            this.ok = ok;
            this.data = data;
            this.error = error;
        }
    }
}