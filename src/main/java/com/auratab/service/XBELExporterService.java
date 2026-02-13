package com.auratab.service;

import com.auratab.model.BookmarkNode;
import com.auratab.model.BookmarksTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class XBELExporterService {
    
    private static final Logger logger = LoggerFactory.getLogger(XBELExporterService.class);
    
    /**
     * 将BookmarksTree导出为XBEL格式的字节数组
     */
    public byte[] exportToXBEL(BookmarksTree tree) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        
        // 创建根元素
        Element xbelElement = document.createElement("xbel");
        xbelElement.setAttribute("version", "1.0");
        document.appendChild(xbelElement);
        
        // 添加头部信息
        Element titleElement = document.createElement("title");
        titleElement.setTextContent("AuraTab Bookmarks");
        xbelElement.appendChild(titleElement);
        
        // 递归添加书签节点
        if (tree.getRoot() != null) {
            addBookmarkNodeToElement(document, xbelElement, tree.getRoot());
        }
        
        // 转换为字节数组
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setOutputProperty("indent", "yes");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        DOMSource source = new DOMSource(document);
        
        transformer.transform(source, result);
        
        return outputStream.toByteArray();
    }
    
    /**
     * 递归地将BookmarkNode添加到XML元素中
     */
    private void addBookmarkNodeToElement(Document document, Element parentElement, BookmarkNode node) {
        if ("folder".equals(node.getType())) {
            // 创建folder元素
            Element folderElement = document.createElement("folder");
            if (node.getId() != null) {
                folderElement.setAttribute("id", node.getId());
            }
            parentElement.appendChild(folderElement);
            
            // 添加标题
            Element titleElement = document.createElement("title");
            titleElement.setTextContent(node.getTitle() != null ? node.getTitle() : "Untitled Folder");
            folderElement.appendChild(titleElement);
            
            // 递归添加子节点
            if (node.getChildren() != null) {
                for (BookmarkNode child : node.getChildren()) {
                    addBookmarkNodeToElement(document, folderElement, child);
                }
            }
        } else if ("link".equals(node.getType())) {
            // 创建bookmark元素
            Element bookmarkElement = document.createElement("bookmark");
            if (node.getId() != null) {
                bookmarkElement.setAttribute("id", node.getId());
            }
            if (node.getUrl() != null) {
                bookmarkElement.setAttribute("href", node.getUrl());
            }
            parentElement.appendChild(bookmarkElement);
            
            // 添加标题
            Element titleElement = document.createElement("title");
            titleElement.setTextContent(node.getTitle() != null ? node.getTitle() : (node.getUrl() != null ? node.getUrl() : "Untitled Link"));
            bookmarkElement.appendChild(titleElement);
        }
    }
}