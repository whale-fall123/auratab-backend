package com.auratab.util;

import com.auratab.config.WebDAVServiceProviderConfig;
import org.springframework.stereotype.Component;

/**
 * WebDAV服务提供商特定的工具类
 */
@Component
public class WebDAVProviderUtils {
    
    /**
     * 检查URL是否为坚果云的WebDAV地址
     */
    public static boolean isJianguoyunUrl(String url) {
        return WebDAVServiceProviderConfig.isJianguoyunUrl(url);
    }
    
    /**
     * 获取默认的坚果云WebDAV路径
     */
    public static String getDefaultJianguoyunPath() {
        return WebDAVServiceProviderConfig.JIANGUOYUN_BASE_URL;
    }
    
    /**
     * 验证坚果云配置的有效性
     */
    public static boolean isValidJianguoyunConfig(String baseUrl, String username, String password) {
        if (!isJianguoyunUrl(baseUrl)) {
            return false;
        }
        
        // 坚果云的用户名通常是邮箱格式
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * 标准化坚果云路径
     * 坚果云的路径结构可能需要特殊处理
     */
    public static String normalizeJianguoyunPath(String basePath, String remotePath) {
        if (!isJianguoyunUrl(basePath)) {
            return remotePath;
        }
        
        // 确保路径格式正确
        String normalizedPath = remotePath;
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        
        // 坚果云可能需要特定的路径处理
        return normalizedPath.isEmpty() ? "/" : "/" + normalizedPath;
    }
}