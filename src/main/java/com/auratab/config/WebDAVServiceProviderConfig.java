package com.auratab.config;

import org.springframework.context.annotation.Configuration;

/**
 * 专门针对坚果云等WebDAV服务的配置
 */
@Configuration
public class WebDAVServiceProviderConfig {
    
    // 坚果云的WebDAV基础URL
    public static final String JIANGUOYUN_BASE_URL = "https://dav.jianguoyun.com/dav/";
    
    // 请求间隔时间（毫秒），用于避免请求频率限制
    public static final long REQUEST_INTERVAL_MS = 200L;
    
    // 连接超时时间（毫秒）
    public static final int CONNECT_TIMEOUT_MS = 10000;
    
    // 读取超时时间（毫秒）
    public static final int READ_TIMEOUT_MS = 30000;
    
    /**
     * 检查是否为坚果云的URL
     */
    public static boolean isJianguoyunUrl(String baseUrl) {
        return baseUrl != null && baseUrl.toLowerCase().contains("jianguoyun.com");
    }
}