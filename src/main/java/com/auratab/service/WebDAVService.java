package com.auratab.service;

import com.auratab.config.WebDAVServiceProviderConfig;
import com.auratab.model.WebDAVConfig;
import com.auratab.util.WebDAVProviderUtils;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebDAVService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDAVService.class);
    
    /**
     * 测试WebDAV连接
     */
    public Map<String, Object> testConnection(WebDAVConfig config) {
        Map<String, Object> result = new HashMap<>();
        
        if (config == null || config.getBaseUrl() == null || config.getUsername() == null || config.getPassword() == null) {
            result.put("ok", false);
            result.put("error", "Configuration is incomplete");
            return result;
        }
        
        // 对坚果云进行特殊验证
        if (WebDAVProviderUtils.isJianguoyunUrl(config.getBaseUrl())) {
            if (!WebDAVProviderUtils.isValidJianguoyunConfig(config.getBaseUrl(), 
                    config.getUsername(), config.getPassword())) {
                result.put("ok", false);
                result.put("error", "Invalid Jianguoyun configuration");
                return result;
            }
        }
        
        Sardine sardine = createSardineClient(config);
        
        try {
            String baseUrl = normalizeUrl(config.getBaseUrl());
            
            // 测试连接 - 列出根目录
            boolean canRead = false;
            boolean canWrite = false;
            
            try {
                // 对于坚果云等服务，添加适当的延迟以避免请求频率限制
                maybeAddDelayForProvider(config.getBaseUrl());
                
                // 尝试读取
                sardine.list(baseUrl);
                canRead = true;
                logger.debug("Successfully connected to WebDAV and verified read access: {}", baseUrl);
                
                // 尝试写入 - 创建一个临时测试文件
                // 对于某些WebDAV服务，可能需要更谨慎地进行写入测试
                String testFilePath = baseUrl + ".auratab_test.tmp";
                
                // 添加延迟后再写入
                maybeAddDelayForProvider(config.getBaseUrl());
                sardine.put(testFilePath, "Auratab connection test file".getBytes());
                
                // 添加延迟后再删除
                maybeAddDelayForProvider(config.getBaseUrl());
                sardine.delete(testFilePath);
                canWrite = true;
                
                logger.debug("Successfully verified write access to WebDAV: {}", baseUrl);
            } catch (IOException e) {
                logger.error("WebDAV access error: {}", e.getMessage());
                result.put("ok", false);
                result.put("error", e.getMessage());
                return result;
            }
            
            result.put("ok", true);
            result.put("canRead", canRead);
            result.put("canWrite", canWrite);
            result.put("baseUrl", baseUrl);
            result.put("isJianguoyun", WebDAVProviderUtils.isJianguoyunUrl(config.getBaseUrl()));
            
        } catch (Exception e) {
            logger.error("Unexpected error during WebDAV test: ", e);
            result.put("ok", false);
            result.put("error", e.getMessage());
        } finally {
            closeSardineClient(sardine);
        }
        
        return result;
    }
    
    /**
     * 使用指定配置下载远程文件
     */
    public byte[] downloadFile(WebDAVConfig config, String remotePath) throws IOException {
        if (config == null || config.getBaseUrl() == null || config.getUsername() == null || config.getPassword() == null) {
            throw new IllegalArgumentException("WebDAV configuration is incomplete");
        }
        
        Sardine sardine = createSardineClient(config);
        
        try {
            String fullPath;
            if (WebDAVProviderUtils.isJianguoyunUrl(config.getBaseUrl())) {
                // 对坚果云路径进行特殊处理
                String normalizedPath = WebDAVProviderUtils.normalizeJianguoyunPath(
                    config.getBaseUrl(), remotePath);
                fullPath = normalizeUrl(config.getBaseUrl()) + 
                          (normalizedPath.startsWith("/") ? normalizedPath.substring(1) : normalizedPath);
            } else {
                fullPath = buildFullPath(config.getBaseUrl(), remotePath);
            }
            
            // 对于坚果云等服务，添加适当的延迟以避免请求频率限制
            maybeAddDelayForProvider(config.getBaseUrl());
            
            return sardine.get(fullPath).readAllBytes();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebDAV URL: " + e.getMessage(), e);
        } finally {
            closeSardineClient(sardine);
        }
    }
    
    /**
     * 使用指定配置上传文件到远程
     */
    public void uploadFile(WebDAVConfig config, String remotePath, byte[] content) throws IOException {
        if (config == null || config.getBaseUrl() == null || config.getUsername() == null || config.getPassword() == null) {
            throw new IllegalArgumentException("WebDAV configuration is incomplete");
        }
        
        Sardine sardine = createSardineClient(config);
        
        try {
            String fullPath;
            if (WebDAVProviderUtils.isJianguoyunUrl(config.getBaseUrl())) {
                // 对坚果云路径进行特殊处理
                String normalizedPath = WebDAVProviderUtils.normalizeJianguoyunPath(
                    config.getBaseUrl(), remotePath);
                fullPath = normalizeUrl(config.getBaseUrl()) + 
                          (normalizedPath.startsWith("/") ? normalizedPath.substring(1) : normalizedPath);
            } else {
                fullPath = buildFullPath(config.getBaseUrl(), remotePath);
            }
            
            // 对于坚果云等服务，添加适当的延迟以避免请求频率限制
            maybeAddDelayForProvider(config.getBaseUrl());
            
            sardine.put(fullPath, content);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebDAV URL: " + e.getMessage(), e);
        } finally {
            closeSardineClient(sardine);
        }
    }
    
    /**
     * 创建Sardine客户端，特别适配坚果云等服务
     */
    private Sardine createSardineClient(WebDAVConfig config) {
        Sardine sardine = SardineFactory.begin(config.getUsername(), config.getPassword());
        
        // 对于坚果云等服务，可能需要设置额外的HTTP头或配置
        // 可以在这里添加特定提供商的配置
        
        return sardine;
    }
    
    /**
     * 关闭Sardine客户端
     */
    private void closeSardineClient(Sardine sardine) {
        if (sardine instanceof AutoCloseable) {
            try {
                ((AutoCloseable) sardine).close();
            } catch (Exception e) {
                logger.warn("Error closing Sardine client: ", e);
            }
        }
    }
    
    /**
     * 规范化URL，确保以斜杠结尾
     */
    private String normalizeUrl(String url) {
        if (url == null) {
            return url;
        }
        return url.endsWith("/") ? url : url + "/";
    }
    
    /**
     * 构建完整路径
     */
    private String buildFullPath(String baseUrl, String remotePath) throws URISyntaxException {
        String normalizedBaseUrl = normalizeUrl(baseUrl);
        
        // 移除remotePath开头的斜杠（如果存在），避免重复斜杠
        String path = remotePath.startsWith("/") ? remotePath.substring(1) : remotePath;
        return normalizedBaseUrl + path;
    }
    
    /**
     * 根据提供商添加适当的延迟，避免请求频率限制
     */
    private void maybeAddDelayForProvider(String baseUrl) {
        if (WebDAVProviderUtils.isJianguoyunUrl(baseUrl)) {
            // 坚果云可能有请求频率限制，添加稍长的延迟
            addDelay(WebDAVServiceProviderConfig.REQUEST_INTERVAL_MS);
        } else {
            // 其他服务也添加一个小延迟以提高稳定性
            addDelay(50); // 50ms延迟
        }
    }
    
    /**
     * 添加延迟
     */
    private void addDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Thread interrupted during delay: ", e);
        }
    }
}