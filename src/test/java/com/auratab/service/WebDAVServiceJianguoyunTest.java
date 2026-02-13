package com.auratab.service;

import com.auratab.config.WebDAVServiceProviderConfig;
import com.auratab.model.RemoteConfig;
import com.auratab.model.SyncConfig;
import com.auratab.model.WebDAVConfig;
import com.auratab.model.AppSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebDAVServiceJianguoyunTest {

    @Test
    void testJianguoyunUrlDetection() {
        assertTrue(WebDAVServiceProviderConfig.isJianguoyunUrl("https://dav.jianguoyun.com/dav/"));
        assertTrue(WebDAVServiceProviderConfig.isJianguoyunUrl("http://dav.jianguoyun.com/dav/"));
        assertTrue(WebDAVServiceProviderConfig.isJianguoyunUrl("https://dav.jianguoyun.com/dav/folder"));
        assertFalse(WebDAVServiceProviderConfig.isJianguoyunUrl("https://dav.example.com/dav/"));
        assertFalse(WebDAVServiceProviderConfig.isJianguoyunUrl(null));
    }

    @Test
    void testJianguoyunDefaultUrl() {
        assertEquals("https://dav.jianguoyun.com/dav/", WebDAVServiceProviderConfig.JIANGUOYUN_BASE_URL);
    }

    @Test
    void testRequestInterval() {
        assertEquals(200L, WebDAVServiceProviderConfig.REQUEST_INTERVAL_MS);
    }
}