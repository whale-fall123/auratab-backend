package com.auratab.service;

import com.auratab.model.BookmarkNode;
import com.auratab.model.BookmarksTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class XBELParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(XBELParserService.class);
    
    /**
     * 将XBEL格式的字节数组解析为BookmarksTree
     */
    public BookmarksTree parseXBEL(byte[] xbelContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        InputStream inputStream = new ByteArrayInputStream(xbelContent);
        Document document = builder.parse(inputStream);
        
        Element rootElement = document.getDocumentElement();
        if (!"xbel".equalsIgnoreCase(rootElement.getNodeName())) {
            throw new IllegalArgumentException("Invalid XBEL file: Root element is not 'xbel'");
        }
        
        BookmarksTree tree = new BookmarksTree();
        BookmarkNode rootNode = parseXBELFolder(rootElement);
        tree.setRoot(rootNode);
        
        return tree;
    }
    
    /**
     * 递归解析XBEL元素为BookmarkNode
     */
    private BookmarkNode parseXBELFolder(Element element) {
        String tagName = element.getNodeName().toLowerCase();
        
        if ("folder".equals(tagName)) {
            BookmarkNode folderNode = new BookmarkNode();
            folderNode.setType("folder");
            folderNode.setId(UUID.randomUUID().toString());
            folderNode.setTitle(getTitleFromElement(element));
            
            List<BookmarkNode> children = new ArrayList<>();
            NodeList childNodes = element.getChildNodes();
            
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    String childTagName = childElement.getNodeName().toLowerCase();
                    
                    if ("title".equals(childTagName)) {
                        // 标题已经在上面处理过了，跳过
                        continue;
                    } else if ("desc".equals(childTagName)) {
                        // 描述信息，暂时忽略
                        continue;
                    } else if ("folder".equals(childTagName)) {
                        children.add(parseXBELFolder(childElement));
                    } else if ("bookmark".equals(childTagName)) {
                        children.add(parseXBELBookmark(childElement));
                    } else if ("separator".equals(childTagName)) {
                        // 分隔符，暂时忽略
                        continue;
                    }
                }
            }
            
            folderNode.setChildren(children);
            return folderNode;
        } else {
            throw new IllegalArgumentException("Expected folder element but got: " + tagName);
        }
    }
    
    /**
     * 解析XBEL bookmark元素为BookmarkNode
     */
    private BookmarkNode parseXBELBookmark(Element element) {
        BookmarkNode bookmarkNode = new BookmarkNode();
        bookmarkNode.setType("link");
        bookmarkNode.setId(UUID.randomUUID().toString());
        bookmarkNode.setTitle(getTitleFromElement(element));
        bookmarkNode.setUrl(element.getAttribute("href"));
        
        return bookmarkNode;
    }
    
    /**
     * 从元素中提取标题
     */
    private String getTitleFromElement(Element element) {
        NodeList titleNodes = element.getElementsByTagName("title");
        if (titleNodes.getLength() > 0) {
            return titleNodes.item(0).getTextContent().trim();
        }
        
        // 如果没有子标题元素，则尝试直接获取文本内容
        String elementText = element.getTextContent().trim();
        if (element.getNodeName().equalsIgnoreCase("title") && !elementText.isEmpty()) {
            return elementText;
        }
        
        // 如果是bookmark元素，使用href作为标题（如果没有title标签的话）
        if (element.getNodeName().equalsIgnoreCase("bookmark")) {
            String href = element.getAttribute("href");
            if (href != null && !href.isEmpty()) {
                return href; // 使用URL作为标题
            }
        }
        
        // 默认标题
        return "Untitled";
    }
}